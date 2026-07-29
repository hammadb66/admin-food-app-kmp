package org.example.adminfoodapp.database.model


import kotlinx.serialization.Serializable

@Serializable
data class OrderDetails(
    var userId: String? = null,
    var userName: String? = null,
    var foodName: List<String>? = null,       // Changed to List
    var foodImages: List<String>? = null,     // Changed to List
    var foodPrice: List<String>? = null,      // Changed to List
    var foodQuantity: List<Int>? = null,      // Changed to List
    var address: String? = null,
    var totalPrice: String? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean? = false,
    var paymentReceived: Boolean? = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0,
    var orderCancel: Boolean? = false,
    var cancelBy: String? = null,
    var cancelConfirm: Boolean? = false,
)
