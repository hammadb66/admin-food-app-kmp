package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.MenuRepository

class GetCategoriesUseCase(
    val repository: MenuRepository
) {

    fun invoke() = repository.getCategories()
}