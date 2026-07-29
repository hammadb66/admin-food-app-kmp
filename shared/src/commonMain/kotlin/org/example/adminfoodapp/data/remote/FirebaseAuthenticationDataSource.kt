package org.example.adminfoodapp.data.remote

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import dev.gitlive.firebase.database.DatabaseReference
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.SessionManager

class FirebaseAuthenticationDataSource(
    private val db: DatabaseReference, private val sessionManager: SessionManager
) {

    fun signUp(user: UserModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val normalizedName = user.name?.trim()
            if (normalizedName.isNullOrBlank()) {
                emit(Resource.Error("Username is required"))
                return@flow
            }

            // ✅ Duplicate check ab naam se
            val existingSnapshot = db.child("admin").orderByChild("name").equalTo(normalizedName).valueEvents.first()

            if (existingSnapshot.exists) {
                emit(Resource.Error("This User Name Not Available"))
                return@flow
            }

            val key = db.child("admin").push().key ?: return@flow emit(Resource.Error("Failed to generate user key"))

            val userWithKey = user.copy(key = key, name = normalizedName)
            db.child("admin").child(key).setValue(userWithKey)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Sign up failed"))
        }
    }

    fun login(username: String, password: String): Flow<Resource<UserModel>> = callbackFlow {
        trySend(Resource.Loading)
        try {
            val normalizedName = username.trim()

            val snapshot = db.child("admin").orderByChild("name").equalTo(normalizedName).valueEvents.first()

            var matchedUser: UserModel? = null
            for (child in snapshot.children) {
                val candidate = child.value<UserModel?>()
                if (candidate?.password == password) {
                    matchedUser = candidate
                    break
                }
            }

            if (matchedUser == null) {
                trySend(Resource.Error("Invalid username or password"))
                close()
                return@callbackFlow
            }

            sessionManager.saveCurrentUserKey(matchedUser.key ?: "")
            trySend(Resource.Success(matchedUser))
            close()
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Login failed"))
            close()
        }
        awaitClose()
    }

    fun currentUser(): Flow<Resource<UserModel>> = callbackFlow {
        trySend(Resource.Loading)
        val key = sessionManager.getCurrentUserKey()
        Log.d("TAG", "currentUser: $key")
        if (key.isNullOrBlank()) {
            trySend(Resource.Error("No active session"))
            close()
            return@callbackFlow
        }

        val userRef = db.child("admin").child(key)
        val job = launch {
            try {
                userRef.valueEvents.collect { snapshot ->
                    val user = snapshot.value<UserModel?>()
                    if (user != null) trySend(Resource.Success(user))
                    else trySend(Resource.Error("User data not found"))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
        awaitClose { job.cancel() }
    }

    suspend fun sessionOut() {
        sessionManager.clearSession()
    }

    suspend fun reauthenticate(password: String): Resource<Unit> {
        val key = sessionManager.getCurrentUserKey() ?: return Resource.Error("No active session")

        return try {
            val snapshot = db.child("admin").child(key).valueEvents.first()
            val user = snapshot.value<UserModel?>()
            if (user?.password == password) Resource.Success(Unit)
            else Resource.Error("Ghalat Password! Dobara check karein.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Re-authentication failed.")
        }
    }

    fun getUserList(): Flow<Resource<List<UserModel>>> = callbackFlow {
        trySend(Resource.Loading)
        val ref = db.child("admin")
        val job = launch {
            try {
                ref.valueEvents.collect { snapshot ->
                    val list = snapshot.children.mapNotNull {
                        try {
                            it.value<UserModel?>()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(Resource.Success(list))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }
        awaitClose { job.cancel() }
    }

    fun updateUser(user: UserModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val key = user.key ?: return@flow emit(Resource.Error("Missing user key"))
            val userMap = mapOf(
                "name" to user.name, "password" to user.password, "admin" to user.admin, "userAccess" to user.userAccess
            ).filterValues { it != null }

            db.child("admin").child(key).updateChildren(userMap)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update profile data"))
        }
    }

    fun updateUserAccess(user: UserModel): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        if (user.key == null) {
            Resource.Error("Failed to update data")
            return@flow
        }
        try {
            val userMap = mapOf(
                "name" to user.name, "password" to user.password, "admin" to user.admin, "userAccess" to user.userAccess
            ).filterValues { it != null }

            db.child("admin").child(user.key).updateChildren(userMap)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update access")
        }
    }


    suspend fun deleteUser(id: String) {
        try {
            db.child("admin").child(id).removeValue()

        } catch (e: Exception) {
        }
    }
}