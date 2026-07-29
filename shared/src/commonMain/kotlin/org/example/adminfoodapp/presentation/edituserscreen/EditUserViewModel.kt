package org.example.adminfoodapp.presentation.edituserscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase
import org.example.adminfoodapp.utils.Resource

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