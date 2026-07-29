package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.domain.repository.MenuRepository

class CancelOrderUseCase (
    val repository: MenuRepository
) {
    fun invoke(orderDetails: OrderDetails) = repository.cancelOrder(orderDetails)
}