package org.example.adminfoodapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.adminfoodapp.data.model.UserModel

class ShareUserItemViewModel(
) : ViewModel() {
    private val _userModel = MutableStateFlow<UserModel?>(null)

    val userModel = _userModel.asStateFlow()


    fun setUserModel(userModel: UserModel) {
        _userModel.value = userModel
    }

    fun removeValue() {
        _userModel.value = null
    }


}