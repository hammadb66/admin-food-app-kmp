package org.example.adminfoodapp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class OneSignalResponse(
    val id: String?,
    val recipients: Int?
)