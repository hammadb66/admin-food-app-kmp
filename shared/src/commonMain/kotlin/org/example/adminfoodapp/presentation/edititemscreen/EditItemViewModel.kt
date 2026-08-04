package org.example.adminfoodapp.presentation.edititemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.AllMenu
import org.example.adminfoodapp.domain.usecase.UpdateMenuItemUseCase
import org.example.adminfoodapp.utils.UploadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.GetCategoriesUseCase
import org.example.adminfoodapp.utils.Resource


class EditItemViewModel(
    private val updateMenuItemUseCase: UpdateMenuItemUseCase, private val getCategoriesUseCase: GetCategoriesUseCase

) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories


    init {
        fetchCategories()
    }


    fun fetchCategories() {

        viewModelScope.launch {
            getCategoriesUseCase.invoke().collect { state ->
                when (state) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        _categories.value = state.data
                    }

                    is Resource.Error -> {}
                }

            }
        }


    }

    fun updateMenuItem(item: AllMenu, byteImage: ByteArray?) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = updateMenuItemUseCase(item, byteImage)

            _uploadState.value = result.fold(
                onSuccess = { UploadState.Success },
                onFailure = { UploadState.Error(it.message ?: "Upload failed") })
        }
    }


    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}