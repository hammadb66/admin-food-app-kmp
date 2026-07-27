package org.example.adminfoodapp.presentation.adminprofilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.GetUsersListUseCase
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AdminViewModel (
    val getUsersListUseCase: GetUsersListUseCase, val updateUserUseCase: UpdateUserAccessUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<UserModel>>>(Resource.Loading)
    val state = _state.asStateFlow()
    private val _stateUpdateUser = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val stateUpdateUser = _stateUpdateUser.asStateFlow()


    init {
        getUserList()
    }

    private fun getUserList() {
        viewModelScope.launch {
            getUsersListUseCase.invoke().collect {
                _state.value = it
            }
        }
    }


    fun updateUserAccess(user: List<UserModel>) {
        viewModelScope.launch {
            updateUserUseCase.invoke(user).collect {
                _stateUpdateUser.value = it
            }
        }
    }

}

