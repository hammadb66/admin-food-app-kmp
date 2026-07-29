package org.example.adminfoodapp.database.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.example.adminfoodapp.database.*

class RestaurantRepository(driverFactory: DriverFactory) {

    private val database = RestaurantDatabase(driverFactory.createDriver())
    private val dbQueries = database.restaurantQueries

    // Tables Operations
    fun getAllTables(): Flow<List<TablesEntity>> = dbQueries.getAllTables().asFlow().mapToList(Dispatchers.IO)
    fun addTable(tableNumber: String, capacity: Long) = dbQueries.insertTable(tableNumber, capacity)
    fun updateTableStatus(tableId: Long, status: String) = dbQueries.updateTableStatus(status, tableId)
    fun deleteTable(tableId: Long) = dbQueries.deleteTable(tableId)

    // Active Table Order Get or Create
    fun getOrCreatePendingOrder(tableId: Long, currentTimeStamp: Long): Long {
        val pendingOrder = dbQueries.getPendingOrderForTable(tableId).executeAsOneOrNull()
        if (pendingOrder != null) {
            return pendingOrder.id
        }

        // Naya Order Banayein
        dbQueries.createNewOrder(
            tableId = tableId,
            orderType = "DINE_IN",
            totalAmount = 0.0,
            createdAt = currentTimeStamp
        )
        // Table Ko Occupied Red Kar dein
        dbQueries.updateTableStatus("OCCUPIED", tableId)

        return dbQueries.getPendingOrderForTable(tableId).executeAsOne().id
    }

    // Order Items Operations
    fun getOrderItems(orderId: Long): Flow<List<OrderItemsEntity>> =
        dbQueries.getOrderItemsForOrder(orderId).asFlow().mapToList(Dispatchers.IO)

    fun addOrIncrementItem(orderId: Long, menuItemId: Long, itemName: String, price: Double) {
        val existingItems = dbQueries.getOrderItemsForOrder(orderId).executeAsList()
        val match = existingItems.find { it.menuItemId == menuItemId }

        if (match != null) {
            dbQueries.updateOrderItemQuantity(match.quantity + 1, match.id)
        } else {
            dbQueries.addOrderItem(orderId, menuItemId, itemName, 1, price)
        }
    }

    fun updateQuantity(orderItemId: Long, newQuantity: Long) {
        if (newQuantity <= 0) {
            dbQueries.deleteOrderItem(orderItemId)
        } else {
            dbQueries.updateOrderItemQuantity(newQuantity, orderItemId)
        }
    }

    fun removeItem(orderItemId: Long) = dbQueries.deleteOrderItem(orderItemId)

    // Checkout & Bill
    fun checkoutOrder(orderId: Long, tableId: Long, finalAmount: Double) {
        dbQueries.completeOrder(finalAmount, orderId)
        dbQueries.updateTableStatus("AVAILABLE", tableId)
    }


    // Dynamic Menu Items Get & Add
    fun getAllMenuItems(): Flow<List<MenuItemsEntity>> =
        dbQueries.getAllMenuItems().asFlow().mapToList(Dispatchers.IO)

    fun addMenuItem(name: String, price: Double, category: String) {
        dbQueries.insertMenuItem(name, price, category)
    }

}