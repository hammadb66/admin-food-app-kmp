package org.example.adminfoodapp.database.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.database.remote.FirebaseAuthenticationDataSource
import org.example.adminfoodapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.example.adminfoodapp.utils.LoginState

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthenticationDataSource
) : AuthRepository {

    override fun signUp(user: UserModel) = dataSource.signUp(user)

    override fun login(email: String, password: String): Flow<LoginState<UserModel>> =
        dataSource.login(email, password)

    override fun currentUser(): Flow<Resource<UserModel>> =
        dataSource.currentUser()

    override fun getUsers() = dataSource.getUserList()


    override suspend fun updateUserAccess(user: UserModel) =
        dataSource.updateUserAccess(user)

    override suspend fun sessionOut() = dataSource.sessionOut()
    override suspend fun deleteUser(id:String) = dataSource.deleteUser(id)

}