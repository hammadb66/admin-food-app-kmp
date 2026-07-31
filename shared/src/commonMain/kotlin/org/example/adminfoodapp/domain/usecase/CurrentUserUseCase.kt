package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.AuthRepository

class CurrentUserUseCase(
    private val repo: AuthRepository
) {
    operator fun invoke() = repo.currentUser()
}