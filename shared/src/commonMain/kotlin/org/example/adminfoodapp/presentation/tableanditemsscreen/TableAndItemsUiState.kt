package org.example.adminfoodapp.presentation.tableanditemsscreen

import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.TablesEntity

data class TableAndItemsUiState(
    val tables: List<TablesEntity> = emptyList(),
    val items: List<GenralItemsEntity> = emptyList(),
    val isTableTab: Boolean = true,
    val showAddDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val itemToDelete: Any? = null ,
    val searchQuery: String = "",
    val itemToEdit: Any? = null
)