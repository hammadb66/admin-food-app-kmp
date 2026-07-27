package org.example.adminfoodapp.presentation.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.LoginUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.utils.Utils.ISADMIN
import org.example.adminfoodapp.utils.Utils.ISUSERACCESS

class LoginViewModel(
    private val loginUseCase: LoginUseCase, private val setting: Settings
) : ViewModel() {

    private val _stateLogin = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val stateLogin: StateFlow<Resource<UserModel>> = _stateLogin

    fun login(email: String, password: String) {
        viewModelScope.launch {

            loginUseCase.invoke(email, password).collect { result ->
                if (result is Resource.Success) {
                    saveUserData(result.data)
                }
                _stateLogin.value = result
            }

        }
    }


    private fun saveUserData(user: UserModel) {
        setting.putBoolean(ISADMIN, user.admin ?: false)
        setting.putBoolean(ISUSERACCESS, user.userAccess ?: false)
    }


}