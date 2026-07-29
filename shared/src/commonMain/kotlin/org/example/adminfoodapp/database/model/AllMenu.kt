package org.example.adminfoodapp.database.model

import kotlinx.serialization.Serializable

@Serializable
data class AllMenu(
    val key: String? = null,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    val foodDescription: String? = null,
    val foodIngredient: String? = null,
)