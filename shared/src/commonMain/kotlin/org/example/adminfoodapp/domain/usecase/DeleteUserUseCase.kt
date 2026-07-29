package org.example.adminfoodapp.domain.usecase

import org.example.adminfoodapp.domain.repository.AuthRepository

class DeleteUserUseCase(val repository: AuthRepository) {

    suspend fun invoke(id:String)=repository.deleteUser(id)
}