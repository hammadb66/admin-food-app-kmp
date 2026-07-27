package org.example.adminfoodapp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val key: String? = null,
    val name: String? = null,
    val password: String? = null,
    val admin: Boolean? = false,
    val userAccess: Boolean? = false
)