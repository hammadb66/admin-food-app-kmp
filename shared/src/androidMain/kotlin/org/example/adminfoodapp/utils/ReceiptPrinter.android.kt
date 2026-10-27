package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.model.PrintOrderDetails

actual object ReceiptPrinter {
    actual fun printReceipt(order: PrintOrderDetails, mode: PrintMode) {
    }

    actual fun printAllOrders(orders: List<PrintOrderDetails>,title: String) {
    }
}