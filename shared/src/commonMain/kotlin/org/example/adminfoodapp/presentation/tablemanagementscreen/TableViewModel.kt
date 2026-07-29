package org.example.adminfoodapp.presentation.tablemanagementscreen

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

class TableViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    // 1. TO-DO: Database Se Real-Time Tables Flow
    val tablesList: StateFlow<List<TablesEntity>> = repository.getAllTables()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // TO-DO: UI Messages Emission (Toasts/Snackbars)
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 2. TO-DO: Add Table Functionality with Validation
    fun addTable(tableNumber: String, capacityInput: String) {
        viewModelScope.launch {
            if (tableNumber.isBlank()) {
                _uiEvent.emit("Table number khali nahi chhod sakte!")
                return@launch
            }

            val capacity = capacityInput.toLongOrNull() ?: 4L

            try {
                repository.addTable(tableNumber.trim(), capacity)
                _uiEvent.emit("Table ${tableNumber.trim()} successfully add ho gaya!")
            } catch (e: Exception) {
                // Duplicate table number UNIQUE constraint fail hone par
                _uiEvent.emit("Error: Ye Table number pehle se exist karta hai!")
            }
        }
    }

    // 3. TO-DO: Toggle Status (AVAILABLE <-> OCCUPIED)
    fun toggleTableStatus(table: TablesEntity) {
        viewModelScope.launch {
            val newStatus = if (table.status == "AVAILABLE") "OCCUPIED" else "AVAILABLE"
            repository.updateTableStatus(table.id, newStatus)
        }
    }

    // 4. TO-DO: Delete Table
    fun deleteTable(tableId: Long) {
        viewModelScope.launch {
            repository.deleteTable(tableId)
            _uiEvent.emit("Table delete kar diya gaya!")
        }
    }
}