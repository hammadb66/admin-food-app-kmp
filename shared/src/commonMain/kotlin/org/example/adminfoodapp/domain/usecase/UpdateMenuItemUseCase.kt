package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.database.model.AllMenu
import org.example.adminfoodapp.domain.repository.MenuRepository

class UpdateMenuItemUseCase(
    private val repo: MenuRepository
) {
    suspend operator fun invoke(item: AllMenu, imageBytes: ByteArray?): Result<Unit> =
        repo.updateMenuItem(item, imageBytes)
}