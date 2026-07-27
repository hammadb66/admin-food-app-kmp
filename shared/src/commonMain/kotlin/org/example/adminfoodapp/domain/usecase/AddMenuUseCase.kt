package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.repository.MenuRepository
import java.io.File

class AddMenuUseCase (private val repo: MenuRepository) {
    suspend operator fun invoke(item: AllMenu, file: File) = repo.addMenu(item, file)
}