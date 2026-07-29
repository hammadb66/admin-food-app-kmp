package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails

interface PendingOrderItemClick {
    fun pendingOrderItemClick(item: OrderDetails)
}