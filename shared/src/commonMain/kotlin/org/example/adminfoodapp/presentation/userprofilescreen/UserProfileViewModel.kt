package org.example.adminfoodapp.presentation.userprofilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.ReauthenticateUseCase
import org.example.adminfoodapp.domain.usecase.UpdateUserUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase

class UserProfileViewModel (
    val currentUserUseCase: CurrentUserUseCase, private val updateUserUseCase: UpdateUserUseCase,
    private val reauthenticateUseCase: ReauthenticateUseCase

) : ViewModel() {


    private val _state = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val state = _state.asStateFlow()
    private val _stateUpdate = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val stateUpdate = _stateUpdate.asStateFlow()

    private val _stateReauth = MutableStateFlow<Resource<Unit>?>(null)
    val stateReauth = _stateReauth.asStateFlow()

    init {
        currentUser()
    }

    fun currentUser() {
        viewModelScope.launch {
            currentUserUseCase.invoke().collect {
                _state.value = it
            }
        }
    }

    fun updateUser(user: UserModel) {
        viewModelScope.launch {
            updateUserUseCase.invoke(user).collect {
                _stateUpdate.value = it
            }

        }
    }


    fun reauthenticateAndRetry(password: String, pendingUser: UserModel) {
        viewModelScope.launch {
            _stateReauth.value = Resource.Loading
            when (val result = reauthenticateUseCase.invoke(password)) {
                is Resource.Success -> {
                    _stateReauth.value = Resource.Success(Unit)
                    // Re-auth succeeded! Now automatically retry the update
                    updateUser(pendingUser)
                }
                is Resource.Error -> {
                    _stateReauth.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun resetReauthState() {
        _stateReauth.value = null
    }

}


