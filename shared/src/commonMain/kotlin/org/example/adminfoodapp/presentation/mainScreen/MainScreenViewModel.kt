package org.example.adminfoodapp.presentation.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.model.QuickPendingOrder
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetPendingOrderUseCase
import org.example.adminfoodapp.domain.usecase.SessionOutUseCase
import org.example.adminfoodapp.utils.MainActivityUiState
import org.example.adminfoodapp.utils.Resource

class MainScreenViewModel(
    private val getCompleteOrderUseCase: GetCompleteOrderUseCase,
    private val getPendingOrderUseCase: GetPendingOrderUseCase,
    private val sessionOutUseCase: SessionOutUseCase,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    private val _localPendingOrders = MutableStateFlow<List<QuickPendingOrder>>(emptyList())
    val localPendingOrders: StateFlow<List<QuickPendingOrder>> = _localPendingOrders.asStateFlow()

    private val _onlinePendingOrders = MutableStateFlow<List<OrderDetails>>(emptyList())
    val onlinePendingOrders: StateFlow<List<OrderDetails>> = _onlinePendingOrders.asStateFlow()

    private val _onlineEarnings = MutableStateFlow(0)
    private val _onlineCompleteCount = MutableStateFlow(0)

    init {
        fetchAllData()
        observeCombinedAnalytics()
    }

    suspend fun sessionOut() {
        sessionOutUseCase.invoke()
    }

    private fun fetchAllData() {
        viewModelScope.launch {
            launch { fetchOnlineCompleteOrders() }
            launch { fetchOnlinePendingOrders() }
            launch { fetchLocalPendingOrders() }
        }
    }

    private suspend fun fetchOnlineCompleteOrders() {
        getCompleteOrderUseCase.invoke().collect { resource ->
            if (resource is Resource.Success) {
                // FIX 1: Sirf un orders ko filter karein jo cancel NAHI hue
                val validCompleteOrders = resource.data.filter { it.orderCancel == false }

                _onlineEarnings.value = totalEarningCollection(validCompleteOrders)
                _onlineCompleteCount.value = validCompleteOrders.size
            }
        }
    }

    private suspend fun fetchOnlinePendingOrders() {
        getPendingOrderUseCase.invoke().collect { resource ->
            if (resource is Resource.Success) {
                _onlinePendingOrders.value = resource.data
            }
        }
    }

    private suspend fun fetchLocalPendingOrders() {
        restaurantRepository.getRealPendingOrders().collect { localOrders ->
            _localPendingOrders.value = localOrders
        }
    }

    // Dynamic Reactive Flow Combination
    private fun observeCombinedAnalytics() {
        viewModelScope.launch {
            combine(
                restaurantRepository.getDashboardAnalytics(),
                _onlineEarnings,
                _onlineCompleteCount,
                _onlinePendingOrders,
                _localPendingOrders
            ) { localAnalytics, onlineEarning, onlineComplete, onlinePendingList, localPendingList ->

                MainActivityUiState(
                    totalEarnings = localAnalytics.totalEarnings + onlineEarning,
                    completeOrdersCount = localAnalytics.completeOrdersCount + onlineComplete,
                    pendingOrdersCount = onlinePendingList.size + localPendingList.size,
                    isLoading = false
                )
            }.collectLatest { combinedState ->
                _uiState.value = combinedState
            }
        }
    }

    private fun totalEarningCollection(data: List<OrderDetails>): Int {
        val price = data.filter { it.orderCancel == false }
        return price.sumOf { order ->
            order.totalPrice?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        }
    }
}