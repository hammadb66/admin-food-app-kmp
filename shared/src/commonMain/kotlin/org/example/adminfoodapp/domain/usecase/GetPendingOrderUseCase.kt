package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.MenuRepository

class GetPendingOrderUseCase(
    val repository: MenuRepository
) {
    fun invoke() = repository.getOrderHistory()
}