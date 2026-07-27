package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.repository.MenuRepository
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.Flow

class GetMenuItemsUseCase (
    private val repository: MenuRepository
) {

    operator fun invoke(): Flow<Resource<MutableList<AllMenu>>> = repository.getMenuItems()

}