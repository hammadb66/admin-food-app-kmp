package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.OrderDetails
import org.example.adminfoodapp.domain.repository.MenuRepository

class OrderDispatchUseCase(
    private val repository: MenuRepository
) {
    fun invoke(orderDetails: OrderDetails) = repository.orderDispatch(orderDetails)
}