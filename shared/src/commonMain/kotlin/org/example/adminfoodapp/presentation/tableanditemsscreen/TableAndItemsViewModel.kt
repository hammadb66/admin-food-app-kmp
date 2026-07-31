package org.example.adminfoodapp.presentation.tableanditemsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.TablesEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.utils.Resource

class TableAndItemsViewModel(private val repository: RestaurantRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TableAndItemsUiState())
    val uiState = _uiState.asStateFlow()

    init { loadAllData() }

    private fun loadAllData() {
        viewModelScope.launch {
            repository.getAllTables().collect { t ->
                _uiState.update { it.copy(tables = t) }
            }
        }

        // Yahan fix lagao:
        viewModelScope.launch {
            repository.getAllMenuItems().collect { resource ->
                if (resource is Resource.Success) {
                    _uiState.update { it.copy(items = resource.data) }
                }
                // Tum yahan Loading ya Error bhi handle kar sakte ho agar chaho
            }
        }
    }


    // TableAndItemsViewModel mein ye function shamil karo
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setTab(isTable: Boolean) { _uiState.update { it.copy(isTableTab = isTable) } }

    // Duplicate Check
    fun addTable(name: String, capacity: Long) {
        if (_uiState.value.tables.any { it.tableNumber.equals(name, true) }) return
        viewModelScope.launch { repository.addTable(tableNumber = name, capacity = capacity) }
    }

    fun addItem(name: String, price: Double) {
        if (_uiState.value.items.any { it.itemName.equals(name, true) }) return
        viewModelScope.launch { repository.addGeneralItem(name = name, price = price,"General Item")}
    }

    fun requestDelete(entity: Any?) {
        _uiState.update { it.copy(itemToDelete = entity) }
    }

    fun confirmDelete() {
        val item = _uiState.value.itemToDelete ?: return // Agar null hai, toh return kar jao

        viewModelScope.launch {
            when (item) {
                is TablesEntity -> repository.deleteTable(item.id)
                is GenralItemsEntity -> repository.deleteGeneralItem(item.id)
            }
        }
        // Delete karne ke baad null set karke dialog band karo
        _uiState.update { it.copy(itemToDelete = null) }
    }





    fun startEditing(entity: Any?) {
        _uiState.update { it.copy(itemToEdit = entity) }
    }

    fun updateTable(id: Long, name: String, capacity: Long) {
        viewModelScope.launch {
            // Tumhari repo mein updateTable ka function hona chahiye
            repository.updateTable(id, name, capacity)
            _uiState.update { it.copy(itemToEdit = null) } // Dialog band
        }
    }

    fun updateItem(id: Long, name: String, price: Double) {
        viewModelScope.launch {
            // Tumhari repo mein updateItem ka function hona chahiye
            repository.updateGeneralItem(id, name, price)
            _uiState.update { it.copy(itemToEdit = null) }
        }
    }
}