package org.example.adminfoodapp.database.remote

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.example.adminfoodapp.database.DriverFactory
import org.example.adminfoodapp.database.RestaurantDatabase
import org.example.adminfoodapp.database.RestaurantDatabase.Companion.invoke
import org.example.adminfoodapp.database.RestaurantQueries
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.utils.LoginState
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.SessionManager

class FirebaseAuthenticationDataSource(driverFactory: DriverFactory,
    private val sessionManager: SessionManager
) {

    private val database = RestaurantDatabase(driverFactory.createDriver())
    private val dbQueries = database.restaurantQueries




    init {
        seedDefaultAdminIfEmpty()
    }

    private fun seedDefaultAdminIfEmpty() {
        try {
            val userCount = dbQueries.getUsersCount().executeAsOne()
            if (userCount == 0L) {
                dbQueries.insertUser(
                    name = "admin",
                    password = "123",
                    isAdmin = 1L,     // 1L = Admin (True)
                    userAccess = 1L   // 1L = Full Access (True)
                )
                println("✅ Default Admin Auto-Created: Username: admin | Password: 123")
            }
        } catch (e: Exception) {
            println("Error seeding admin: ${e.message}")
        }
    }

    // 1. SIGN UP (Naya User Add Karna)
    fun signUp(user: UserModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val normalizedName = user.name?.trim()
            if (normalizedName.isNullOrBlank()) {
                emit(Resource.Error("Username is required"))
                return@flow
            }

            // Check karein username pehle se exist to nahi karta
            val existingUser = dbQueries.getUserByName(normalizedName).executeAsOneOrNull()
            if (existingUser != null) {
                emit(Resource.Error("This User Name Not Available"))
                return@flow
            }

            // Insert into SQLite DB (Boolean to 0/1 Long conversion)
            dbQueries.insertUser(
                name = normalizedName,
                password = user.password ?: "",
                isAdmin = if (user.admin == true) 1L else 0L,
                userAccess = if (user.userAccess == true) 1L else 0L
            )

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Sign up failed"))
        }
    }.flowOn(Dispatchers.IO)

    // 2. LOGIN (Verify Credentials & Save Active Operator Session)
    fun login(username: String, password: String): Flow<LoginState<UserModel>> = flow {
        emit(LoginState.Loading)
        try {
            val normalizedName = username.trim()
            val userEntity = dbQueries.getUserByName(normalizedName).executeAsOneOrNull()

            if (userEntity == null || userEntity.password != password) {
                emit(LoginState.Error("Invalid username or password"))
                return@flow
            }

            // Convert SQLite Entity to UserModel (1L == true, 0L == false)
            val matchedUser = UserModel(
                key = userEntity.id.toString(),
                name = userEntity.name,
                password = userEntity.password,
                admin = userEntity.isAdmin == 1L,
                userAccess = userEntity.userAccess == 1L
            )

            // 🔥 SECURITY SESSION: ID aur Active Name dono local settings mein save kar rahe hain
            sessionManager.saveSession(
                userKey = matchedUser.key ?: "",
                userName = matchedUser.name ?: "Admin"
            )

            emit(LoginState.Success(matchedUser))
        } catch (e: Exception) {
            emit(LoginState.Error(e.message ?: "Login failed"))
        }
    }.flowOn(Dispatchers.IO)

    // 3. CURRENT USER (Active Session Fetching)
    fun currentUser(): Flow<Resource<UserModel>> = flow {
        emit(Resource.Loading)
        val key = sessionManager.getCurrentUserKey()
        if (key.isNullOrBlank()) {
            emit(Resource.Error("No active session"))
            return@flow
        }

        val userId = key.toLongOrNull() ?: 0L

        dbQueries.getUserById(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .collect { entity ->
                if (entity != null) {
                    val user = UserModel(
                        key = entity.id.toString(),
                        name = entity.name,
                        password = entity.password,
                        admin = entity.isAdmin == 1L,
                        userAccess = entity.userAccess == 1L
                    )
                    emit(Resource.Success(user))
                } else {
                    emit(Resource.Error("User data not found"))
                }
            }
    }

    // 4. SESSION LOGOUT
    suspend fun sessionOut() {
        sessionManager.clearSession()
    }

    // 5. GET ALL USERS LIST (Admin Panel Ke Liye)
    fun getUserList(): Flow<Resource<List<UserModel>>> = flow {
        emit(Resource.Loading)
        try {
            dbQueries.getAllUsers()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .collect { entityList ->
                    val userList = entityList.map { entity ->
                        UserModel(
                            key = entity.id.toString(),
                            name = entity.name,
                            password = entity.password,
                            admin = entity.isAdmin == 1L,
                            userAccess = entity.userAccess == 1L
                        )
                    }
                    emit(Resource.Success(userList))
                }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load users"))
        }
    }

    // 6. UPDATE USER ACCESS & PERMISSIONS
    fun updateUserAccess(user: UserModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val userId = user.key?.toLongOrNull()
        if (userId == null) {
            emit(Resource.Error("Missing user key"))
            return@flow
        }

        try {
            val normalizedName = user.name?.trim()
            if (normalizedName.isNullOrBlank()) {
                emit(Resource.Error("Username is required"))
                return@flow
            }

            dbQueries.updateUserAccess(
                id = userId,
                name = normalizedName,
                password = user.password ?: "",
                isAdmin = if (user.admin == true) 1L else 0L,
                userAccess = if (user.userAccess == true) 1L else 0L
            )

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update access"))
        }
    }.flowOn(Dispatchers.IO)

    // 7. DELETE USER
    suspend fun deleteUser(id: String) {
        val userId = id.toLongOrNull() ?: return
        try {
            dbQueries.deleteUserById(userId)
        } catch (_: Exception) { }
    }
}