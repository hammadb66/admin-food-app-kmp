package org.example.adminfoodapp.database.model
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailItemRow(
    val name: String,
    val price: String,
    val image: String,
    val quantity: Int
)