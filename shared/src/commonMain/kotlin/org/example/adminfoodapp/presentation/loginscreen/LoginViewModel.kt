package org.example.adminfoodapp.presentation.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.example.adminfoodapp.utils.LoginState
import org.example.adminfoodapp.utils.SessionManager // 👈 Import karein
import org.example.adminfoodapp.utils.Utils.ISADMIN
import org.example.adminfoodapp.utils.Utils.ISUSERACCESS
import org.example.adminfoodapp.utils.Utils.IS_DEVELOPER

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val setting: Settings,
    private val sessionManager: SessionManager // 👈 Add karein
) : ViewModel() {

    private val _stateLogin = MutableStateFlow<LoginState<UserModel>>(LoginState.Idle)
    val stateLogin: StateFlow<LoginState<UserModel>> = _stateLogin

    fun login(email: String, password: String) {
        viewModelScope.launch {
            setting.putBoolean(IS_DEVELOPER, false)
            loginUseCase.invoke(email, password).collect { result ->
                if (result is LoginState.Success) {
                    saveUserData(result.data)
                }
                _stateLogin.value = result
            }
        }
    }

    fun loginAsDeveloper(email: String, password: String) {
        viewModelScope.launch {
            _stateLogin.value = LoginState.Loading
            try {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                val uid = Firebase.auth.currentUser?.uid ?: throw Exception("User ID not found")

                val devRef = Firebase.database.reference().child("dev").child(uid)
                val devSnapshot = devRef.valueEvents.first()
                val isDev = devSnapshot.value<Boolean?>() ?: false

                if (!isDev) {
                    Firebase.auth.signOut()
                    throw Exception("Access Denied. You are not a Developer.")
                }

                setting.putBoolean(IS_DEVELOPER, true)
                setting.putBoolean(ISADMIN, true)

                // 👇 YEH SABSE ZAROORI HAI: Session save karein taake Firebase data load ho
                sessionManager.saveSession(userKey = uid, userName = email.substringBefore("@"))

                val devUser = UserModel(
                    name = email.substringBefore("@"),
                    admin = true,
                    userAccess = true
                )
                _stateLogin.value = LoginState.Success(devUser)

            } catch (e: Exception) {
                setting.putBoolean(IS_DEVELOPER, false)
                _stateLogin.value = LoginState.Error("Dev Login Failed: ${e.message}")
            }
        }
    }

    private fun saveUserData(user: UserModel) {
        setting.putBoolean(ISADMIN, user.admin ?: false)
        setting.putBoolean(ISUSERACCESS, user.userAccess ?: false)
    }
}