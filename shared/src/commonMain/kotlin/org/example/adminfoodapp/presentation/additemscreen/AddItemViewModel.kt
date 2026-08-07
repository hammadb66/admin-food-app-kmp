package org.example.adminfoodapp.presentation.additemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.adminfoodapp.database.model.AllMenu
import org.example.adminfoodapp.domain.usecase.AddMenuUseCase
import org.example.adminfoodapp.utils.UploadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.domain.usecase.GetCategoriesUseCase
import org.example.adminfoodapp.utils.NetworkMonitor
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.ToastManager

class AddItemViewModel(
    private val addMenuUseCase: AddMenuUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val networkMonitor: NetworkMonitor,
    private val toastManager: ToastManager
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories


    init {
        checkInterNetConnection()
    }

    fun checkInterNetConnection() {
        networkMonitor.register(onAvailable = {
            fetchCategories()
        }, onLost = {
            _uploadState.value = UploadState.Error("Check your internet connection")
            toastManager.show("No internet connection")
        })
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

    fun uploadItem(item: AllMenu, imageBytes: ByteArray) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = addMenuUseCase(item, imageBytes)

            _uploadState.value = result.fold(
                onSuccess = { UploadState.Success },
                onFailure = { UploadState.Error(it.message ?: "Upload failed") })
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}