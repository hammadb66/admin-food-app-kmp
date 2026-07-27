package org.example.adminfoodapp.domain.repository

interface NotificationRepository {
    suspend fun sendOrderNotification(
        userId: String,
        title: String,
        message: String
    ): Result<Unit>
}