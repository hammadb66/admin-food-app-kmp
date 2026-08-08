package org.example.adminfoodapp.presentation.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private val _onlineCompleteOrders = MutableStateFlow<List<OrderDetails>>(emptyList())

    init {
        fetchAllData()
        observeCombinedAnalytics()
    }

    suspend fun sessionOut() {
        sessionOutUseCase.invoke()
    }

    private fun fetchAllData() {
        // 1. Fetch Online Completed Orders
        viewModelScope.launch {
            getCompleteOrderUseCase.invoke().collect { resource ->
                if (resource is Resource.Success) {
                    // Double filter khatam kar diya
                    _onlineCompleteOrders.value = resource.data.filter { it.orderCancel == false }
                }
            }
        }

        // 2. Fetch Online Pending Orders
        viewModelScope.launch {
            getPendingOrderUseCase.invoke().collect { resource ->
                if (resource is Resource.Success) {
                    _onlinePendingOrders.value = resource.data
                }
            }
        }

        // 3. Fetch Local Pending Orders
        viewModelScope.launch {
            restaurantRepository.getRealPendingOrders().collect { localOrders ->
                _localPendingOrders.value = localOrders
            }
        }
    }

    // Combined Reactive Flow
    private fun observeCombinedAnalytics() {
        viewModelScope.launch {
            combine(
                restaurantRepository.getDashboardAnalytics(),
                _onlineCompleteOrders,
                _onlinePendingOrders,
                _localPendingOrders
            ) { localAnalytics, onlineCompleteList, onlinePendingList, localPendingList ->

                val onlineEarning = totalEarningCollection(onlineCompleteList)

                MainActivityUiState(
                    totalOnlineEarnings = onlineEarning,
                    totalLocalEarnings = localAnalytics.totalLocalEarnings,
                    onlineCompleteOrders = onlineCompleteList.size,
                    localCompleteOrders = localAnalytics.localCompleteOrders,
                    onlinePendingOrders = onlinePendingList.size,
                    localPendingOrders = localPendingList.size,
                    isLoading = false
                )
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }

    // Fast & Safe String parsing
    private fun totalEarningCollection(data: List<OrderDetails>): Int {
        return data.sumOf { order ->
            order.totalPrice?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        }
    }
}