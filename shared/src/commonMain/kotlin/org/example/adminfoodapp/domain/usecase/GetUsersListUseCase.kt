package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.AuthRepository

class GetUsersListUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.getUsers()
}
