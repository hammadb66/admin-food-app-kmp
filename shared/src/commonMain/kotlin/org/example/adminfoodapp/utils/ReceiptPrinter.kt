package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails

enum class PrintMode { THERMAL, A4 }

expect object ReceiptPrinter {
    fun printReceipt(order: OrderDetails, mode: PrintMode)
    fun printAllOrders(orders: List<OrderDetails>)
}