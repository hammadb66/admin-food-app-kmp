package org.example.adminfoodapp.presentation.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.LoginUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase

class LoginViewModel (
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _stateLogin = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val stateLogin: StateFlow<Resource<UserModel>> = _stateLogin
    fun login(email: String, password: String) {
        viewModelScope.launch {

            loginUseCase.invoke(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _stateLogin.value = Resource.Loading

                    }

                    is Resource.Success -> {
                        _stateLogin.value = Resource.Success(result.data)


                    }

                    is Resource.Error -> {
                        _stateLogin.value = Resource.Error(result.message)


                    }


                }
            }

        }
    }

}