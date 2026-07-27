package org.example.adminfoodapp.domain.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signUp(user: UserModel): Flow<Resource<Unit>>
    fun login(email: String, password: String): Flow<Resource<UserModel>>
    fun currentUser(): Flow<Resource<UserModel>>
    fun getUsers(): Flow<Resource<List<UserModel>>>
    fun updateUser(user: UserModel): Flow<Resource<Unit>>
    suspend fun reauthenticate(password: String): Resource<Unit>
    suspend fun updateUserAccess(user: List<UserModel>): Resource<Unit>

   suspend fun sessionOut()
}