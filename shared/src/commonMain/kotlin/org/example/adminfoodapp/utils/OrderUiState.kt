package org.example.adminfoodapp.utils

sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    object Success : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}