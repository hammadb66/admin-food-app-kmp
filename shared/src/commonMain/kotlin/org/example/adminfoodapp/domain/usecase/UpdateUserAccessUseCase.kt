package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.repository.AuthRepository

class UpdateUserAccessUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: List<UserModel>) = authRepository.updateUserAccess(user)

}