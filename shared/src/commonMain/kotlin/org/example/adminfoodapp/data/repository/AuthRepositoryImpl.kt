package org.example.adminfoodapp.data.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.data.remote.FirebaseAuthenticationDataSource
import org.example.adminfoodapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthenticationDataSource
) : AuthRepository {

    override fun signUp(user: UserModel) = dataSource.signUp(user)

    override fun login(email: String, password: String): Flow<Resource<UserModel>> =
        dataSource.login(email, password)

    override fun currentUser(): Flow<Resource<UserModel>> =
        dataSource.currentUser()

    override fun getUsers() = dataSource.getUserList()

    override fun updateUser(user: UserModel): Flow<Resource<Unit>> =
        dataSource.updateUser(user)

    override suspend fun reauthenticate(password: String): Resource<Unit> =
        dataSource.reauthenticate(password)

    override suspend fun updateUserAccess(user: UserModel) =
        dataSource.updateUserAccess(user)

    override suspend fun sessionOut() = dataSource.sessionOut()
    override suspend fun deleteUser(id:String) = dataSource.deleteUser(id)

}