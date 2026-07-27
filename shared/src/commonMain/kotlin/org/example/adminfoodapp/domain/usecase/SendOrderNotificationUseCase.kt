package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.utils.OrderStatus
import org.example.adminfoodapp.domain.repository.NotificationRepository

class SendOrderNotificationUseCase (
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(userId: String, status: OrderStatus): Result<Unit> {
        val messages = mapOf(
            OrderStatus.CANCELLED to "Aapka order cancel kar diya gaya hai",
            OrderStatus.ACCEPTED to "Aapka order accept ho gaya hai",
            OrderStatus.DISPATCHED to "Aapka order dispatch ho gaya hai",
        )
        return repository.sendOrderNotification(
            userId = userId,
            title = "Order Update",
            message = messages[status] ?: "Order status update hua hai"
        )
    }
}