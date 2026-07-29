package org.example.adminfoodapp.presentation.createuserscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.domain.usecase.SignUpUseCase
import org.example.adminfoodapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateUserViewModel(
    val signUpUseCase: SignUpUseCase
) : ViewModel() {

   private val _signUpState = MutableStateFlow<Resource<Unit>?>(null)
    val signUpState = _signUpState.asStateFlow()

    fun signUpUser(user: UserModel) {
        viewModelScope.launch {
            signUpUseCase.invoke(user).collect {
                _signUpState.value = it
            }
        }
    }

}


