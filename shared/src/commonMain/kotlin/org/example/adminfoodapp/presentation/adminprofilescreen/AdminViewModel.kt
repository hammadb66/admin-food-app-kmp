package org.example.adminfoodapp.presentation.adminprofilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.GetUsersListUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.DeleteUserUseCase


class AdminViewModel(
    val getUsersListUseCase: GetUsersListUseCase, val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<UserModel>>>(Resource.Loading)
    val state = _state.asStateFlow()


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


    fun deleteConfirm(id: String?) {
        println(id)
        if (id == null) {
            return
        }
        viewModelScope.launch {
            deleteUserUseCase.invoke(id)
        }
    }

}

