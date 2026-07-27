package org.example.adminfoodapp.data.remote

import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.utils.Resource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FirebaseAuthenticationDataSource(
    private val auth: FirebaseAuth,
    private val db: DatabaseReference,
    private val context: Any   // Koin se aata hai — Android Context ya dummy Application() (desktop)
) {

    fun signUp(user: UserModel): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)
        val secondaryAppName = "AdminUserCreator"

        val secondaryApp: FirebaseApp = try {
            Firebase.app(secondaryAppName)
        } catch (e: Exception) {
            Firebase.initialize(
                context = context,
                options = Firebase.app.options,
                name = secondaryAppName
            )
        }

        val secondaryAuth = Firebase.auth(secondaryApp)

        try {
            val result = secondaryAuth.createUserWithEmailAndPassword(user.email!!, user.password!!)
            val newUid = result.user?.uid

            if (newUid.isNullOrBlank()) {
                trySend(Resource.Error("Failed to retrieve new user UID."))
                secondaryAuth.signOut()
                awaitClose()
                return@callbackFlow
            }

            try {
                result.user?.sendEmailVerification()
            } catch (emailException: Exception) {
                trySend(Resource.Error("User created, but verification email failed: ${emailException.message}"))
                secondaryAuth.signOut()
                close()
                return@callbackFlow
            }

            val userWithKey = user.copy(key = newUid)

            saveUser(userWithKey).collect { result ->
                when (result) {
                    is Resource.Success -> trySend(Resource.Success(Unit))
                    is Resource.Error -> trySend(Resource.Error(result.message))
                    else -> {}
                }
            }
            secondaryAuth.signOut()
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Sign up failed due to an unknown error."))
        }
        awaitClose()
    }

    fun saveUser(saveUser: UserModel): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)
        try {
            db.child("admin").child(saveUser.key!!).setValue(saveUser)
            trySend(Resource.Success(Unit))
            close()
        } catch (e: Exception) {
            trySend(Resource.Error("Issue in creating user"))
            close()
        }
        awaitClose()
    }

    fun login(email: String, password: String): Flow<Resource<UserModel>> = callbackFlow {
        trySend(Resource.Loading)

        try {
            auth.signInWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            trySend(Resource.Error("Login failed: ${e.message}"))
            close()
            return@callbackFlow
        }

        val user = auth.currentUser
        if (user == null) {
            trySend(Resource.Error("User session not found."))
            close()
            return@callbackFlow
        }

        if (!user.isEmailVerified) {
            trySend(Resource.Error("Please verify your email before logging in."))
            auth.signOut()
            close()
            return@callbackFlow
        }

        val userRef = db.child("admin").child(user.uid)

        val job = launch {
            try {
                userRef.valueEvents.collect { snapshot ->
                    val admin = snapshot.value<UserModel?>()
                    if (admin != null) {
                        trySend(Resource.Success(admin))
                    } else {
                        trySend(Resource.Error("User data is empty."))
                    }
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }

        awaitClose { job.cancel() }
    }

    fun currentUser(): Flow<Resource<UserModel>> = callbackFlow {
        trySend(Resource.Loading)

        val user = auth.currentUser
        if (user == null) {
            trySend(Resource.Error("User session not found."))
            close()
            return@callbackFlow
        }

        if (!user.isEmailVerified) {
            trySend(Resource.Error("Please verify your email before logging in."))
            auth.signOut()
            close()
            return@callbackFlow
        }

        val userRef = db.child("admin").child(user.uid)

        val job = launch {
            try {
                userRef.valueEvents.collect { snapshot ->
                    val admin = snapshot.value<UserModel?>()
                    if (admin != null) {
                        trySend(Resource.Success(admin))
                    } else {
                        trySend(Resource.Error("User data is empty."))
                    }
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }

        awaitClose { job.cancel() }
    }

    suspend fun reauthenticate(password: String): Resource<Unit> {
        val currentUser = auth.currentUser ?: return Resource.Error("User session not found.")

        if (password.isEmpty()) return Resource.Error("Password cannot be empty.")

        return try {
            currentUser.getIdToken(true)
            currentUser.reload()

            val email = currentUser.email ?: return Resource.Error("User email not found.")

            val credential = EmailAuthProvider.credential(email, password)

            currentUser.reauthenticate(credential)

            Resource.Success(Unit)
        } catch (e: Exception) {
            val msg = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Ghalat Password! Dobara check karein."
                else -> e.message ?: "Re-authentication failed."
            }
            Resource.Error(msg)
        }
    }

    fun getUserList(): Flow<Resource<List<UserModel>>> = callbackFlow {
        trySend(Resource.Loading)

        val databaseReference = db.child("admin")

        val job = launch {
            try {
                databaseReference.valueEvents.collect { snapshot ->
                    val userList = mutableListOf<UserModel>()
                    for (userSnapshot in snapshot.children) {
                        try {
                            val user = userSnapshot.value<UserModel?>()
                            user?.let { userList.add(it) }
                        } catch (e: Exception) {
                            // corrupted node ko skip karo, crash mat hone do
                        }
                    }
                    trySend(Resource.Success(userList))
                }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message.toString()))
            }
        }

        awaitClose { job.cancel() }
    }

    fun updateUser(user: UserModel): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)
        val currentUser = auth.currentUser

        if (currentUser == null) {
            trySend(Resource.Error("User session not found."))
            close()
            return@callbackFlow
        }
        if (currentUser.uid != user.key) {
            trySend(Resource.Error("You can only update your own profile."))
            close()
            return@callbackFlow
        }

        try {
            val targetEmail = user.email
            if (!targetEmail.isNullOrBlank() && targetEmail != currentUser.email) {
                currentUser.verifyBeforeUpdateEmail(targetEmail)
            }

            val targetPassword = user.password
            if (!targetPassword.isNullOrBlank()) {
                currentUser.updatePassword(targetPassword)
                currentUser.getIdToken(true)
            }

            val userMap = mapOf(
                "name" to user.name,
                "email" to user.email,
                "key" to user.key,
                "admin" to user.admin,
                "userAccess" to user.userAccess
            ).filterValues { it != null }

            db.child("admin").child(user.key!!).updateChildren(userMap)

            trySend(Resource.Success(Unit))
            close()

        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            trySend(Resource.Error("REAUTH_REQUIRED"))
            close()
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Failed to update profile data"))
            close()
        }

        awaitClose()
    }

    suspend fun updateUserAccess(changedUsers: List<UserModel>): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            if (changedUsers.isEmpty()) {
                emit(Resource.Success(Unit))
                return@flow
            }

            val updates = mutableMapOf<String, Any?>()
            changedUsers.forEach { user ->
                val uid = user.key
                if (!uid.isNullOrEmpty()) {
                    updates["$uid/userAccess"] = user.userAccess
                }
            }

            if (updates.isNotEmpty()) {
                db.child("admin").updateChildren(updates)
            }

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update access"))
        }
    }
}