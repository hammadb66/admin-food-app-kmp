package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.domain.repository.MenuRepository

class OrderAcceptedUseCase(
    private val repository: MenuRepository
){
    fun invoke(orderDetails: OrderDetails)=repository.orderAccepted(orderDetails)
}