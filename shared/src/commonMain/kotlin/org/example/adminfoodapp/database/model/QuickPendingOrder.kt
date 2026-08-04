package org.example.adminfoodapp.database.model


data class QuickPendingOrder(
    val id: String,
    val orderType: String,
    val titleOrTable: String,
    val itemCount: Int,
    val totalAmount: Double,
    val timeAgo: String = "Active Now",
    val targetTab: String = ""
)