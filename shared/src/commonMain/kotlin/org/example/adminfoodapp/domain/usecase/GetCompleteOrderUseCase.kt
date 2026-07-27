package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.MenuRepository

class GetCompleteOrderUseCase  (
    private val repository: MenuRepository
) {

    fun invoke() = repository.getCompleteOrder()
}

