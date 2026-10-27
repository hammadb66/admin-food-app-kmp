package org.example.adminfoodapp.presentation.edituserscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase
import org.example.adminfoodapp.utils.Resource

class EditUserViewModel(
    val updateAccessUseCase: UpdateUserAccessUseCase
) : ViewModel() {

    fun updateUser(user: UserModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            updateAccessUseCase(user).collect { state ->
                if (state is Resource.Success) {     // sirf Success pe
                    onSuccess()
                }
            }
        }
    }


}