    package org.example.adminfoodapp.database.repository

    import app.cash.sqldelight.coroutines.asFlow
    import app.cash.sqldelight.coroutines.mapToList
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.withContext
    import org.example.adminfoodapp.database.*
    import org.example.adminfoodapp.database.model.QuickPendingOrder
    import org.example.adminfoodapp.utils.MainActivityUiState
    import org.example.adminfoodapp.utils.ORDERSTATUS
    import org.example.adminfoodapp.utils.ORDERTYPE
    import org.example.adminfoodapp.utils.Resource
    import org.example.adminfoodapp.utils.SessionManager
    import org.example.adminfoodapp.utils.TABLESTATUS
    import java.util.Calendar


    // Helper model for Batch Void Logging
    data class VoidLogBatchItem(
        val orderId: Long, val itemName: String, val cancelledQuantity: Int, val unitPrice: Double
    )

    class RestaurantRepository(
        driverFactory: DriverFactory, private val sessionManager: SessionManager
    ) {

        private val database = RestaurantDatabase(driverFactory.createDriver())
        private val dbQueries = database.restaurantQueries


        // Active Table Order Get or Create
        fun getOrCreatePendingOrder(
            customerName: String, customerNumber: Long, tableId: Long, currentTimeStamp: Long
        ): Long {
            val pendingOrder = dbQueries.getPendingOrderForTable(tableId).executeAsOneOrNull()
            if (pendingOrder != null) {
                return pendingOrder.id
            }

            // Naya Order Banayein
            dbQueries.createNewOrder(
                tableId = tableId,
                orderType = ORDERTYPE.DINEIN.id,
                customerName = customerName,
                customerNumber = customerNumber,
                totalAmount = 0.0,
                createdAt = currentTimeStamp,
                createdByID = sessionManager.getCurrentUserKey().toString().toLong(),
                createdByName = sessionManager.getCurrentUserName()
            )
            // Table Ko Occupied Red Kar dein
            dbQueries.updateTableStatus(1, tableId)

            return dbQueries.getPendingOrderForTable(tableId).executeAsOne().id
        }

        fun addOrIncrementItem(orderId: Long, menuItemId: Long, itemName: String, price: Double) {
            val existingItems = dbQueries.getOrderItemsForOrder(orderId).executeAsList()
            val match = existingItems.find { it.menuItemId == menuItemId }

            if (match != null) {
                println("match")

                dbQueries.updateOrderItemQuantity(match.quantity + 1, match.id)
            } else {
                println("not match")

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

        fun addGeneralItem(name: String, price: Double, category: String) {
            dbQueries.insertMenuItem(name, price, category)
        }


        fun updateCommittedQuantity(orderItemId: Long, newQuantity: Long) {
            if (newQuantity <= 0) {
                dbQueries.deleteOrderItem(orderItemId)
            } else {
                dbQueries.updateOrderItemQuantity(newQuantity, orderItemId)
            }
        }

        fun removeCommittedItem(orderItemId: Long) = dbQueries.deleteOrderItem(orderItemId)


        // Tables Operations
        fun getAllTables(): Flow<List<TablesEntity>> = dbQueries.getAllTables().asFlow().mapToList(Dispatchers.IO)
        fun addTable(tableNumber: String, capacity: Long) = dbQueries.insertTable(tableNumber, capacity)
    //    fun updateTableStatus(tableId: Long, status: String) = dbQueries.updateTableStatus(status, tableId)
        fun deleteTable(tableId: Long) = dbQueries.deleteTable(tableId)
        fun deleteGeneralItem(tableId: Long) = dbQueries.deleteGeneralItem(tableId)

        // Check if Active Order exists
        fun getPendingOrderForTable(tableId: Long): OrdersEntity? {
            return dbQueries.getPendingOrderForTable(tableId).executeAsOneOrNull()
        }
        fun getPendingOrderForTakeAway(): OrdersEntity?{
            return dbQueries.getPendingOrderForTakeAway().executeAsOneOrNull()
        }

        // Create Order and Occupy Table
        fun createOrderAndOccupyTable(
            customerName: String, customerNumber: Long, tableId: Long, currentTimeStamp: Long
        ): Long {
            dbQueries.createNewOrder(
                tableId = tableId,
                orderType = ORDERTYPE.DINEIN.id,
                customerName = customerName,
                customerNumber = customerNumber,
                totalAmount = 0.0,
                createdAt = currentTimeStamp,
                createdByID = sessionManager.getCurrentUserKey().toString().toLong(),
                createdByName = sessionManager.getCurrentUserName()
            )
            dbQueries.updateTableStatus(1, tableId)
            return dbQueries.getPendingOrderForTable(tableId).executeAsOne().id
        }

    //    // Commit Draft Items into Database (Initial printedQuantity = 0)
    //    fun commitDraftItems(orderId: Long, draftItems: List<DraftItem>) {
    //        val existingItems = dbQueries.getOrderItemsForOrder(orderId).executeAsList()
    //
    //        draftItems.forEach { draft ->
    //            val match = existingItems.find { it.menuItemId == draft.menuItemId }
    //            if (match != null) {
    //                // Pehle se mavjood item me quantity barhain
    //                dbQueries.updateOrderItemQuantity(match.quantity + draft.quantity, match.id)
    //            } else {
    //                // Naya item insert karain
    //                dbQueries.addOrderItem(
    //                    orderId = orderId,
    //                    menuItemId = draft.menuItemId,
    //                    itemName = draft.itemName,
    //                    quantity = draft.quantity.toLong(),
    //                    price = draft.price
    //                )
    //            }
    //        }
    //    }

        // Observe Saved DB Items
        fun getOrderItems(orderId: Long): Flow<List<OrderItemsEntity>> =
            dbQueries.getOrderItemsForOrder(orderId).asFlow().mapToList(Dispatchers.IO)

        // Increase Saved Item Quantity (Increases 'quantity' BUT leaves 'printedQuantity' as-is for delta print)
        fun increaseCommittedItemQuantity(orderItemId: Long, newQuantity: Long) {
            dbQueries.updateOrderItemQuantity(newQuantity, orderItemId)
        }


        // Update Printed Quantity to Match Total Quantity after Printing
        fun markOrderAsPrinted(orderId: Long) {
            dbQueries.updatePrintedQuantity(orderId)
        }

        // Complete Order & Free Table
    // Complete Order & Free Table
        fun checkoutOrder(
            customerName: String, customerNumber: String, orderId: Long, tableId: Long?, finalAmount: Double
        ) {
            val phoneNumber = customerNumber.toLongOrNull() ?: 0L   // ✅ crash-proof

            dbQueries.updateOrderCustomerInfo(customerName = customerName, customerNumber = phoneNumber,id=orderId )
            dbQueries.completeOrder(finalAmount, orderId)

            // Agar Table ID majood thi toh hi AVAILABLE mark karo
            if (tableId != null && tableId > 0) {
                dbQueries.updateTableStatus(0, tableId)
            }
        }

        fun createOrder(
            customerName: String, customerNumber: Long, tableId: Long?, orderType: Long, currentTimeStamp: Long
        ): Long {
            return database.transactionWithResult {
                dbQueries.createNewOrder(
                    tableId = tableId ?: 0L,
                    orderType = orderType,
                    customerName = customerName,
                    customerNumber = customerNumber,
                    totalAmount = 0.0,
                    createdAt = currentTimeStamp,
                    createdByID = sessionManager.getCurrentUserKey().toString().toLong(),
                    createdByName = sessionManager.getCurrentUserName()
                )

                // Sirf Dine-In orders par table status OCCUPIED hoga
                if (orderType == ORDERTYPE.DINEIN.id && tableId != null && tableId > 0) {
                    dbQueries.updateTableStatus(1, tableId)
                    dbQueries.getPendingOrderForTable(tableId).executeAsOne().id
                } else {
                    // Takeaway ke liye last inserted order ID return karein
                    dbQueries.getLastInsertedOrderId().executeAsOne()
                }
            }
        }

        // Catalog Menu
        fun getAllMenuItems(): Flow<Resource<List<GenralItemsEntity>>> = flow {
            emit(Resource.Loading)
            try {
                dbQueries.getAllMenuItems().asFlow().mapToList(Dispatchers.IO).collect { filterList ->
                    emit(Resource.Success(filterList))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error"))
            }
        }


        // Save/Update Items in DB & Sync printedQuantity after Printing
        fun commitAndPrintOrder(
            orderId: Long, itemsToSave: List<Triple<Long?, Long, String>>, // (dbId, menuItemId, itemName)
            quantities: Map<Long, Int>, // menuItemId -> target quantity
            prices: Map<Long, Double>
        ) {
            database.transaction {
                itemsToSave.forEach { (dbId, menuItemId, itemName) ->
                    val targetQty = quantities[menuItemId] ?: 0
                    val price = prices[menuItemId] ?: 0.0

                    if (dbId != null) {
                        if (targetQty <= 0) {
                            dbQueries.deleteOrderItem(dbId)
                        } else {
                            dbQueries.updateOrderItemQuantity(targetQty.toLong(), dbId)
                        }
                    } else if (targetQty > 0) {
                        dbQueries.addOrderItem(
                            orderId = orderId,
                            menuItemId = menuItemId,
                            itemName = itemName,
                            quantity = targetQty.toLong(),
                            price = price
                        )
                    }
                }
                // Sync printedQuantity = quantity for all items of this order
                dbQueries.updatePrintedQuantity(orderId)
            }
        }

        // Process Batch Void Log & Apply Decrements in DB
        fun processBatchVoid(customerName: String,
            orderId: Long, tableId: Long, // 👈 1. Pass tableId here
            voidItems: List<VoidLogBatchItem>, reason: String, decreasedItemsMap: Map<Long, Int>
        ) {
            database.transaction {
                // 1. Insert Void Audit Trail Logs
                val timeStamp = System.currentTimeMillis()
                voidItems.forEach { voidItem ->
                    dbQueries.insertVoidLog(
                        orderId = voidItem.orderId,
                        itemName = voidItem.itemName,
                        cancelledQuantity = voidItem.cancelledQuantity.toLong(),
                        unitPrice = voidItem.unitPrice,
                        reason = reason.ifBlank { "Customer Cancelled / Decreased" },
                        createdAt = timeStamp,
                        voidByID = sessionManager.getCurrentUserKey().toString().toLong(),
                        voidedByName = sessionManager.getCurrentUserName(),
                        customerName=customerName
                    )
                }

                // 2. Update/Delete items in DB
                decreasedItemsMap.forEach { (dbId, newQty) ->
                    if (newQty <= 0) {
                        dbQueries.deleteOrderItem(dbId)
                    } else {
                        dbQueries.updateOrderItemQuantity(newQty.toLong(), dbId)
                        dbQueries.updatePrintedQuantity(orderId)
                    }
                }

                // 👈 3. CHECK IF ORDER IS NOW EMPTY
                val remainingItems = dbQueries.getOrderItemsForOrder(orderId).executeAsList()
                if (remainingItems.isEmpty()) {
                    // Table ko wapas Green / AVAILABLE kar do!
                    dbQueries.updateTableStatus(0, tableId)
                    // Empty order ko finish/close kar do
                    dbQueries.completeOrder(0.0, orderId)
                }
            }
        }

        fun updateTable(id: Long, tableId: String, capacity: Long) {
            dbQueries.updateTable(tableId, capacity, id)
        }

        fun updateGeneralItem(id: Long, name: String, price: Double) {
            dbQueries.updateGeneralItem(name, price, id)

        }

        // Date & Search Query ke mutabiq Orders History
        fun getOrdersHistory(startOfDay: Long, endOfDay: Long, searchQuery: String): Flow<List<OrdersEntity>> {
            return dbQueries.getOrdersHistory(startOfDay, endOfDay, searchQuery).asFlow().mapToList(Dispatchers.IO)
        }

        // Date Range ke mutabiq Void Audit Logs History
        fun getVoidLogsHistory(startOfDay: Long, endOfDay: Long): Flow<List<VoidLogsEntity>> {
            return dbQueries.getVoidLogsHistory(startOfDay, endOfDay).asFlow().mapToList(Dispatchers.IO)
        }

        // Specific Order ke items fetch karein (Re-print ke liye)
        suspend fun getOrderItemsForReprint(orderId: Long): List<OrderItemsEntity> {
            return withContext(Dispatchers.IO) {
                dbQueries.getOrderItemsForOrder(orderId).executeAsList()
            }
        }
        // 1. Dashboard Sales & Revenue Analytics Real DB Query


        suspend fun updateOrderCustomerInfo(orderId: Long, customerName: String, customerNumber: Long) {
            dbQueries.updateOrderCustomerInfo(customerName = customerName, customerNumber = customerNumber, id = orderId)
        }

        // Repository Methods Inside RestaurantRepositoryImpl Class
        fun getDashboardAnalytics(): Flow<MainActivityUiState> = flow {
            val calendar = java.util.Calendar.getInstance()

            // Today Start & End Timestamps
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val startOfToday = calendar.timeInMillis

            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
            calendar.set(java.util.Calendar.MINUTE, 59)
            calendar.set(java.util.Calendar.SECOND, 59)
            val endOfToday = calendar.timeInMillis

            // Queries to SQLDelight DB
            val todayOrders = dbQueries.getOrdersHistory(startOfToday, endOfToday, "").executeAsList()
            val activeTables = dbQueries.getAllTables().executeAsList().filter { it.status == TABLESTATUS.OCCUPIED.id }

            // Calculations
            val todayTotalEarnings = todayOrders.sumOf { it.totalAmount }
            val completedOrdersToday = todayOrders.count { it.orderStatus == ORDERSTATUS.COMPLETE.id }
            val pendingOrdersToday = todayOrders.count { it.orderStatus == ORDERSTATUS.PENDING.id }

            emit(
                MainActivityUiState(
                    totalLocalEarnings = todayTotalEarnings.toInt(),
                    localCompleteOrders = completedOrdersToday,
                    localPendingOrders = activeTables.size + pendingOrdersToday,
                    isLoading = false,
                    errorMessage = null
                )
            )
        }

        fun getRealPendingOrders(): Flow<List<QuickPendingOrder>> = flow {
            // JADOO: While loop har 2 second baad automatically UI ko refresh karega
            while (true) {
                val pendingList = mutableListOf<QuickPendingOrder>()

                // 1. Fetch Active Dine-In Tables Orders
                val allActiveTables = dbQueries.getAllTables().executeAsList().filter { it.status == TABLESTATUS.OCCUPIED.id }
                allActiveTables.forEach { table ->
                    val pendingOrder = dbQueries.getPendingOrderForTable(table.id).executeAsOneOrNull()
                    if (pendingOrder != null) {
                        val items: List<OrderItemsEntity> = dbQueries.getOrderItemsForOrder(pendingOrder.id).executeAsList()
                        val totalAmt = items.sumOf { it.quantity * it.price }

                        pendingList.add(
                            QuickPendingOrder(
                                id = pendingOrder.id.toString(),
                                orderType = pendingOrder.orderType,
                                titleOrTable = "Table #${table.tableNumber}",
                                itemCount = items.sumOf { it.quantity.toInt() },
                                totalAmount = totalAmt,
                                timeAgo = "Active Now",
                                targetTab = "DASHBOARD_HOME"
                            )
                        )
                    }
                }

                // 2. Direct SQLDelight Query for Pending Takeaways (Clean & Fast!)
                val takeawayOrders = dbQueries.getPendingTakeawayOrNullOrders().executeAsList()

                takeawayOrders.forEach { order ->
                    val items: List<OrderItemsEntity> = dbQueries.getOrderItemsForOrder(order.id).executeAsList()
                    val totalAmt = items.sumOf { it.quantity * it.price }

                    pendingList.add(
                        QuickPendingOrder(
                            id = order.id.toString(),
                            orderType = ORDERTYPE.TAKEAWAY.id,
                            titleOrTable = "Takeaway - ${order.customerName.ifBlank { "Customer" }}",
                            itemCount = items.sumOf { it.quantity.toInt() },
                            totalAmount = totalAmt,
                            timeAgo = "Active Now",
                            targetTab = "DASHBOARD_HOME"
                        )
                    )
                }

                // Fresh data Dashboard UI ko emit karein
                emit(pendingList)

                // 2 Second wait karein phir dobara live check karein
                delay(2000L)
            }
        }


    }