package org.example.adminfoodapp.database.model

data class PrintOrderDetails(
    val userName: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val foodName: List<String>? = null,
    val foodQuantity: List<Int>? = null,
    val foodPrice: List<String>? = null,
    val totalPrice: String? = null,
    val subtotal: String? = null,
    val discountLabel: String? = null,
    val discountAmount: String? = null
)