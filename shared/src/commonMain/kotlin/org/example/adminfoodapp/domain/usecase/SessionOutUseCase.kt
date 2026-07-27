package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.AuthRepository

class SessionOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() =  repository.sessionOut()


}