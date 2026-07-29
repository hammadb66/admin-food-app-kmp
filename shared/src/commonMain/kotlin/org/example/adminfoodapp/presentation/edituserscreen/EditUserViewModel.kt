package org.example.adminfoodapp.presentation.edituserscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase

class EditUserViewModel(
    val updateAccessUseCase: UpdateUserAccessUseCase
) : ViewModel() {

    fun updateUser(user: UserModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            updateAccessUseCase(user).collect {
                onSuccess()
            }
        }
    }


}