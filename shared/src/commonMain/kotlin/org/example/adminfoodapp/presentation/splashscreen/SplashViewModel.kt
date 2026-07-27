package org.example.adminfoodapp.presentation.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.utils.NetworkMonitor
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.utils.Utils.ISADMIN
import org.example.adminfoodapp.utils.Utils.ISUSERACCESS

class SplashViewModel(
    private val currentUserUseCase: CurrentUserUseCase,
    private val networkMonitor: NetworkMonitor,
    private val settings: Settings,
    private val toastManager: ToastManager
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val state: StateFlow<Resource<UserModel>> = _state

    fun checkConnectionAndFetchUser() {
        networkMonitor.register(
            onAvailable = {
                currentUser()
            },
            onLost = {
                toastManager.show("No internet connection")
            }
        )
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