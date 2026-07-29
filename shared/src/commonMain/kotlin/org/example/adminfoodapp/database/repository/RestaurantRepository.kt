package org.example.adminfoodapp.database.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.example.adminfoodapp.database.DriverFactory
import org.example.adminfoodapp.database.GetPendingOrdersWithTables
import org.example.adminfoodapp.database.RestaurantDatabase
import org.example.adminfoodapp.database.TablesEntity

class RestaurantRepository(driverFactory: DriverFactory) {

    private val database = RestaurantDatabase(driverFactory.createDriver())
    private val dbQueries = database.restaurantQueries

    // 1. Live Tables Stream (Flow)
    fun getAllTables(): Flow<List<TablesEntity>> {
        return dbQueries.getAllTables()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // 2. Naya Table Add Karna
    fun addTable(tableNumber: String, capacity: Long) {
        dbQueries.insertTable(
            tableNumber = tableNumber,
            capacity = capacity,
            status = "AVAILABLE"
        )
    }

    // 3. Table Status Change (AVAILABLE <-> OCCUPIED)
    fun updateTableStatus(tableId: Long, newStatus: String) {
        dbQueries.updateTableStatus(
            status = newStatus,
            id = tableId
        )
    }

    // 4. Table Delete Karna
    fun deleteTable(tableId: Long) {
        dbQueries.deleteTable(id = tableId)
    }

    // 5. Dynamic Attached Orders Flow (JOIN Query)
    fun getPendingOrders(): Flow<List<GetPendingOrdersWithTables>> {
        return dbQueries.getPendingOrdersWithTables()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }
}