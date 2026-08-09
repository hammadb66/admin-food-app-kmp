package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.PrintOrderDetails

enum class PrintMode { THERMAL, A4 }

expect object ReceiptPrinter {
    fun printReceipt(order: PrintOrderDetails, mode: PrintMode)
    fun printAllOrders(orders: List<PrintOrderDetails>,title: String)
}