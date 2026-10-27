package org.example.adminfoodapp.presentation.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.AppTrialManager // 👈 IMPORT KIA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.utils.SessionManager
import org.example.adminfoodapp.utils.Utils.ISADMIN
import org.example.adminfoodapp.utils.Utils.ISUSERACCESS
import java.io.File

class SplashViewModel(
    private val currentUserUseCase: CurrentUserUseCase,
    private val settings: Settings,
    private val sessionManager: SessionManager

) : ViewModel() {

    private val _state = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val state: StateFlow<Resource<UserModel>> = _state

    // UI ko batane ke liye ke Trial chal raha he ya khatam ho gaya
    private val _isTrialActive = MutableStateFlow<Boolean>(true)
    val isTrialActive: StateFlow<Boolean> = _isTrialActive

    fun checkConnectionAndFetchUser() {
        // 🔥 SECURITY: App open hote hi session clear kar do taake har baar login maange
        sessionManager.clearSession()

        _isTrialActive.value = true
        // Direct login screen pe bhejne ke liye error state trigger karein
        _state.value = Resource.Error("Please login to continue")
        return
/*        val appDataDir = File(System.getProperty("user.home"), ".adminfoodapp")
        val isValid = AppTrialManager.isTrialValid(settings, appDataDir)

        if (isValid) {
            _isTrialActive.value = true
            currentUser()
        } else {
            _isTrialActive.value = false
            _state.value = Resource.Error("Trial Expired or Date Tampered!")
        }*/
    }

    fun currentUser() {
        viewModelScope.launch {
            currentUserUseCase.invoke().collect { result ->
                if (result is Resource.Success) {
                    saveUserData(result.data)
                }
                _state.value = result
            }
        }
    }

    private fun saveUserData(user: UserModel) {
        settings.putBoolean(ISADMIN, user.admin ?: false)
        settings.putBoolean(ISUSERACCESS, user.userAccess ?: false)
    }
}