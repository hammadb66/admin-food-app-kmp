package org.example.adminfoodapp.presentation.dineinscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.TablesEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository

class DineInViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    val tablesList: StateFlow<List<TablesEntity>> = repository.getAllTables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}