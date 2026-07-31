package org.example.adminfoodapp.presentation.addgeneralitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.repository.RestaurantRepository

class AddGeneralItemViewModel  ( private val repository: RestaurantRepository
) : ViewModel() {
    fun addNewGeneralItem(name: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.addGeneralItem(name, price, category)
        }
    }

}

