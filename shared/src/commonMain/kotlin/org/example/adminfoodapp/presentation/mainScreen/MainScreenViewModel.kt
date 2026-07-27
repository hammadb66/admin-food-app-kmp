package org.example.adminfoodapp.presentation.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.OrderDetails
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetPendingOrderUseCase
import org.example.adminfoodapp.utils.MainActivityUiState
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.SessionOutUseCase


class MainScreenViewModel(
    private val getCompleteOrderUseCase: GetCompleteOrderUseCase,
    private val getPendingOrderUseCase: GetPendingOrderUseCase,
    private val sessionOutUseCase: SessionOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllData()
    }

   suspend fun sessionOut() {
        sessionOutUseCase.invoke()
    }

    private fun fetchAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Parallel execution for better performance
            launch { fetchCompleteOrdersAndEarnings() }
            launch { fetchPendingOrders() }
        }
    }

    private suspend fun fetchCompleteOrdersAndEarnings() {
        getCompleteOrderUseCase.invoke().collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val earnings = totalEarningCollection(resource.data)
                    _uiState.update {
                        it.copy(
                            totalEarnings = earnings, completeOrdersCount = resource.data.size, isLoading = false
                        )
                    }
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        errorMessage = "Network Error", isLoading = false
                    )
                }

                else -> {}
            }
        }
    }

    private suspend fun fetchPendingOrders() {
        getPendingOrderUseCase.invoke().collect { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(pendingOrdersCount = resource.data.size) }
            }
        }
    }

    private fun totalEarningCollection(data: List<OrderDetails>): Int {
        val price = data.filter { it.orderCancel == false }
        Log.d("tag-priceFilter", "totalEarningCollection: $price")
        return price.sumOf { order ->
            order.totalPrice?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        }
    }
}