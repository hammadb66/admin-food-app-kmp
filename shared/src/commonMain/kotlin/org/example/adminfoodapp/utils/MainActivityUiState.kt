package org.example.adminfoodapp.utils

data class MainActivityUiState(
    val totalOnlineEarnings: Int = 0,
    val totalLocalEarnings: Int = 0,
    val onlinePendingOrders: Int = 0,
    val onlineCompleteOrders: Int = 0,
    val localPendingOrders: Int = 0,
    val localCompleteOrders: Int = 0,
    val takeawayCount: Int = 0,
    val activeTablesCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)