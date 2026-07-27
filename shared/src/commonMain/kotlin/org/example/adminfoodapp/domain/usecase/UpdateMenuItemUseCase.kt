package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.repository.MenuRepository
import java.io.File

class UpdateMenuItemUseCase (
    val repository: MenuRepository
) {
    suspend operator fun invoke(item: AllMenu, newFile: File?)=repository.updateMenuItem(item,newFile)
}