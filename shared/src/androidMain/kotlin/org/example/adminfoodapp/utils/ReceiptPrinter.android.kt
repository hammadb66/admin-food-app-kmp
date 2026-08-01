package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails

actual object ReceiptPrinter {
    actual fun printReceipt(order: OrderDetails, mode: PrintMode) {
    }

    actual fun printAllOrders(orders: List<OrderDetails>) {
    }
}