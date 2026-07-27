package org.example.adminfoodapp.presentation.additemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.domain.usecase.AddMenuUseCase
import org.example.adminfoodapp.utils.UploadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File


class AddItemViewModel (
    private val addMenuUseCase: AddMenuUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun upload(item: AllMenu, file: File) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = addMenuUseCase(item, file)

            _uploadState.value = result.fold(
                onSuccess = { UploadState.Success },
                onFailure = { UploadState.Error(it.message ?: "Upload failed") }
            )
        }
    }
}