package org.example.adminfoodapp.presentation.tablemanagementscreen

import androidx.lifecycle.ViewModel
import org.example.adminfoodapp.database.repository.RestaurantRepository

class TableManagementViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    // Dynamic Database Call
    val tables = restaurantRepository.getAllTables()

    fun addNewTable(tableNum: String, capacity: Long) {
        restaurantRepository.addTable(tableNum, capacity)
    }
}