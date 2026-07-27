package org.example.adminfoodapp.presentation.outofdeliveryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.OrderDetails
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OutOfDeliveryViewModel (
    private val getCompleteOrderUseCase: GetCompleteOrderUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<MutableList<OrderDetails>>>(Resource.Loading)
    val state = _state

    init {
        getCompleteOrder()
    }

    fun getCompleteOrder() {
        viewModelScope.launch {
            getCompleteOrderUseCase.invoke().collect {
                _state.value = it
            }
        }
    }


}