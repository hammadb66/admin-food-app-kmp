package org.example.adminfoodapp.utils

data class MainActivityUiState(
    val totalEarnings: Int = 0,
    val completeOrdersCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
