package org.example.adminfoodapp.presentation.userprofilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase

class UserProfileViewModel (
    private val updateUserAccessUseCase: UpdateUserAccessUseCase,
    private val currentUserUseCase: CurrentUserUseCase,

    ) : ViewModel() {


    private val _stateCurrentUser = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
    val state = _stateCurrentUser.asStateFlow()
    private val _stateUpdate = MutableStateFlow<Resource<Unit>?>(null)
    val stateUpdate = _stateUpdate.asStateFlow()


    init {
        currentUser()
    }

    fun currentUser() {
        viewModelScope.launch {
            currentUserUseCase.invoke().collect {
                _stateCurrentUser.value = it
            }
        }
    }

    fun updateUser(user: UserModel) {
        viewModelScope.launch {
            updateUserAccessUseCase.invoke(user).collect {
                _stateUpdate.value = it
            }

        }
    }
    fun resetValue(){
        _stateUpdate.value = null
    }

}


