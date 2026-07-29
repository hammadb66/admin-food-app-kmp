package org.example.adminfoodapp.utils

import org.example.adminfoodapp.database.model.AllMenu

data class MenuState(
    val menuItems: List<AllMenu> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)