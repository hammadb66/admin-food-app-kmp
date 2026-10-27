package org.example.adminfoodapp.presentation.addgeneralitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.utils.UploadState

class AddGeneralItemViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    fun addNewGeneralItem(name: String, price: Double, category: String) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                repository.addGeneralItem(name, price, category)
                _uploadState.value = UploadState.Success
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Failed to add item")
            }
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}