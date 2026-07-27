package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.repository.AuthRepository

class SignUpUseCase(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(user: UserModel) = repo.signUp(user)
    }
