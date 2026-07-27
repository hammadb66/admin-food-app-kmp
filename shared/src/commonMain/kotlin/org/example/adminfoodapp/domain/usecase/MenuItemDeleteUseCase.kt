package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.MenuRepository

class MenuItemDeleteUseCase (
    val repository: MenuRepository
) {
     operator fun invoke(id:String) = repository.menuItemDelete(id)

}