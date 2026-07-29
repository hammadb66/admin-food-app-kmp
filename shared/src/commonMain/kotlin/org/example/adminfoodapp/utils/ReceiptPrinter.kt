package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.OrderDetails

expect object ReceiptPrinter {
    fun printReceipt(order: OrderDetails)
}