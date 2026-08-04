package org.example.adminfoodapp.domain.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.database.model.UserModel
import kotlinx.coroutines.flow.Flow
import org.example.adminfoodapp.utils.LoginState

interface AuthRepository {
    fun signUp(user: UserModel): Flow<Resource<Unit>>
    fun login(email: String, password: String): Flow<LoginState<UserModel>>
    fun currentUser(): Flow<Resource<UserModel>>
    fun getUsers(): Flow<Resource<List<UserModel>>>
    suspend fun updateUserAccess(user: UserModel): Flow<Resource<Unit>>

   suspend fun sessionOut()

    suspend fun deleteUser(id: String)
}