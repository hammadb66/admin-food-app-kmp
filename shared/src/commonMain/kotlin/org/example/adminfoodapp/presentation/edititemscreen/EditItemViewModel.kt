package org.example.adminfoodapp.presentation.edititemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.usecase.UpdateMenuItemUseCase
import org.example.adminfoodapp.utils.UploadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File


class EditItemViewModel  constructor(
    private val updateMenuItemUseCase: UpdateMenuItemUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun updateMenuItem(item: AllMenu, file: File?) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = updateMenuItemUseCase(item, file)

            _uploadState.value = result.fold(
                onSuccess = { UploadState.Success },
                onFailure = { UploadState.Error(it.message ?: "Upload failed") }
            )
        }
    }
}