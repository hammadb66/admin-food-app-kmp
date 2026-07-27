package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.AuthRepository

class ReauthenticateUseCase (
    private val repository: AuthRepository
) {
    suspend operator fun invoke(password: String) = repository.reauthenticate(password)
}