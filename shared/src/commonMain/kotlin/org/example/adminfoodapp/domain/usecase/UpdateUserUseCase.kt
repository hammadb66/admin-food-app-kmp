package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.repository.AuthRepository

class UpdateUserUseCase  (
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: UserModel) = repository.updateUser(user)
}