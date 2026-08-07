package org.example.adminfoodapp.presentation.outofdeliveryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.utils.NetworkMonitor
import org.example.adminfoodapp.utils.ToastManager

class OutOfDeliveryViewModel (
    private val getCompleteOrderUseCase: GetCompleteOrderUseCase,
    private val networkMonitor: NetworkMonitor,
    private val toastManager: ToastManager
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<MutableList<OrderDetails>>>(Resource.Loading)
    val state = _state

    init {
        checkInterNetConnection()
    }


    fun checkInterNetConnection() {
        networkMonitor.register(onAvailable = {
            getCompleteOrder()
        }, onLost = {
            _state.value = Resource.Error("No internet connection\n Check your internet connection")
            toastManager.show("No internet connection")
        })
    }
    fun getCompleteOrder() {
        viewModelScope.launch {
            getCompleteOrderUseCase.invoke().collect {
                _state.value = it
            }
        }
    }


}