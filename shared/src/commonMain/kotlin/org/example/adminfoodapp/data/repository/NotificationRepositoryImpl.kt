package org.example.adminfoodapp.data.repository

import org.example.adminfoodapp.utils.Utils.ONESIGNAL_APP_ID
import org.example.adminfoodapp.utils.Utils.ONESIGNAL_REST_API_KEY
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import org.example.adminfoodapp.data.model.OneSignalRequest
import org.example.adminfoodapp.data.remote.OneSignalApi
import org.example.adminfoodapp.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val api: OneSignalApi
) : NotificationRepository {

    override suspend fun sendOrderNotification(
        userId: String,
        title: String,
        message: String
    ): Result<Unit> {
        return try {
            val request = OneSignalRequest(
                app_id = ONESIGNAL_APP_ID,
                include_external_user_ids = listOf(userId),
                headings = mapOf("en" to title),
                contents = mapOf("en" to message)
            )

            val response = api.sendNotification(
                authKey = "Basic $ONESIGNAL_REST_API_KEY",
                body = request
            )

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}