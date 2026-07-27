package org.example.adminfoodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OneSignalRequest(
    val app_id: String,
    val include_external_user_ids: List<String>,
    val headings: Map<String, String>,
    val contents: Map<String, String>,
    val data: Map<String, String>? = null
)

