package org.example.adminfoodapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.adminfoodapp.database.model.AllMenu

class SharedEditItemViewModel : ViewModel() {

    private val _itemToEdit = MutableStateFlow<AllMenu?>(null)
    val itemToEdit: StateFlow<AllMenu?> = _itemToEdit

    fun setItemToEdit(item: AllMenu) {
        _itemToEdit.value = item
    }

    fun clearItemToEdit() {
        _itemToEdit.value = null
    }
}