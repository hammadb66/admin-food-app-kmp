package org.example.adminfoodapp.database.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.adminfoodapp.database.RestaurantDatabase

class DatabaseBackupService(private val database: RestaurantDatabase) {

    private val queries = database.restaurantQueries
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // ------------------------------------------------
    // EXPORT: DB → JSON string
    // ------------------------------------------------
    suspend fun exportToJsonString(): String = withContext(Dispatchers.IO) {
        val backup = DatabaseBackup(
            tables = queries.getAllTables().executeAsList().map {
                TableBackup(it.id, it.tableNumber, it.capacity, it.status)
            },
            menuItems = queries.getAllMenuItems().executeAsList().map {
                MenuItemBackup(it.id, it.itemName, it.price, it.category)
            },
            orders = queries.getAllOrdersRaw().executeAsList().map {
                OrderBackup(
                    id = it.id, tableId = it.tableId, customerName = it.customerName,
                    customerNumber = it.customerNumber, orderType = it.orderType,
                    subtotal = it.subtotal, discountType = it.discountType,
                    discountValue = it.discountValue, discountAmount = it.discountAmount,
                    totalAmount = it.totalAmount, orderStatus = it.orderStatus,
                    createdAt = it.createdAt, createdByID = it.createdByID,
                    createdByName = it.createdByName
                )
            },
            orderItems = queries.getAllOrderItemsRaw().executeAsList().map {
                OrderItemBackup(
                    id = it.id, orderId = it.orderId, menuItemId = it.menuItemId,
                    itemName = it.itemName, quantity = it.quantity,
                    printedQuantity = it.printedQuantity, price = it.price
                )
            },
            voidLogs = queries.getAllVoidLogsRaw().executeAsList().map {
                VoidLogBackup(
                    id = it.id, orderId = it.orderId, itemName = it.itemName,
                    cancelledQuantity = it.cancelledQuantity, unitPrice = it.unitPrice,
                    reason = it.reason, createdAt = it.createdAt,
                    voidByID = it.voidByID, voidedByName = it.voidedByName,
                    customerName = it.customerName
                )
            },
            users = queries.getAllUsers().executeAsList().map {
                UserBackup(it.id, it.name, it.password, it.isAdmin, it.userAccess)
            }
        )
        json.encodeToString(DatabaseBackup.serializer(), backup)
    }

    // ------------------------------------------------
    // IMPORT: JSON string → DB (destructive)
    // ------------------------------------------------
    suspend fun importFromJsonString(jsonString: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val backup = json.decodeFromString(DatabaseBackup.serializer(), jsonString)

                // Basic sanity check
                if (backup.version < 1) {
                    return@withContext Result.failure(Exception("Unsupported backup version"))
                }

                database.transaction {
                    // Clear all tables (children first)
                    queries.deleteAllOrderItems()
                    queries.deleteAllVoidLogs()
                    queries.deleteAllOrders()
                    queries.deleteAllMenuItems()
                    queries.deleteAllTables()
                    queries.deleteAllUsers()

                    // Restore (parents first)
                    backup.users.forEach {
                        queries.restoreUser(it.id, it.name, it.password, it.isAdmin, it.userAccess)
                    }
                    backup.tables.forEach {
                        queries.restoreTable(it.id, it.tableNumber, it.capacity, it.status)
                    }
                    backup.menuItems.forEach {
                        queries.restoreMenuItem(it.id, it.itemName, it.price, it.category)
                    }
                    backup.orders.forEach {
                        queries.restoreOrder(
                            it.id, it.tableId, it.customerName, it.customerNumber,
                            it.orderType, it.subtotal, it.discountType, it.discountValue,
                            it.discountAmount, it.totalAmount, it.orderStatus, it.createdAt,
                            it.createdByID, it.createdByName
                        )
                    }
                    backup.orderItems.forEach {
                        queries.restoreOrderItem(
                            it.id, it.orderId, it.menuItemId, it.itemName,
                            it.quantity, it.printedQuantity, it.price
                        )
                    }
                    backup.voidLogs.forEach {
                        queries.restoreVoidLog(
                            it.id, it.orderId, it.itemName, it.cancelledQuantity,
                            it.unitPrice, it.reason, it.createdAt, it.voidByID,
                            it.voidedByName, it.customerName
                        )
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
}