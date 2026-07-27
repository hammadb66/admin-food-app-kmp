package org.example.adminfoodapp.utils

import org.example.adminfoodapp.data.model.OrderDetails

interface OnOrderItemClickListener {
    fun onOderAcceptClick(order: OrderDetails?)
    fun onOderDispatchClick(order: OrderDetails?)
    fun onOderCancelClick(order: OrderDetails)
    fun onOderCancelConfirmClick(order: OrderDetails)

}