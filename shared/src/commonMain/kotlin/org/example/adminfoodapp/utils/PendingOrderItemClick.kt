package org.example.adminfoodapp.utils

import org.example.adminfoodapp.data.model.OrderDetails

interface PendingOrderItemClick {
    fun pendingOrderItemClick(item: OrderDetails)
}