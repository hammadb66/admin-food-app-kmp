package org.example.adminfoodapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.data.model.OrderDetails

class SharedOrderItemViewModel : ViewModel() {

    private val _orderItem = MutableStateFlow<OrderDetails?>(null)
    val orderItem: StateFlow<OrderDetails?> = _orderItem

    fun setOrderItem(item: OrderDetails) {
        _orderItem.value = item
    }

    fun clearOrderItem() {
        _orderItem.value = null
    }
}