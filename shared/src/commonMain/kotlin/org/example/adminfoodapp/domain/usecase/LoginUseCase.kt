package org.example.adminfoodapp.domain.usecase


import org.example.adminfoodapp.domain.repository.AuthRepository

class LoginUseCase (
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String)= repo.login(email, password)

}