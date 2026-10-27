package org.example.adminfoodapp.database.backup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.example.adminfoodapp.database.RestaurantDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AutoBackupService(private val database: RestaurantDatabase) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val queries = database.restaurantQueries
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private const val MAX_BACKUPS_PER_LOCATION = 30
        private const val BACKUP_PREFIX = "auto_"
    }

    // Called from RestaurantRepository after every checkout / void
    fun scheduleBackup() {
        scope.launch {
            try {
                val backupJson = buildBackupJson()
                val fileName = generateFileName()
                saveEverywhere(backupJson, fileName)
                rotateOldBackups()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun buildBackupJson(): String {
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
        return json.encodeToString(DatabaseBackup.serializer(), backup)
    }

    private fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "$BACKUP_PREFIX$timestamp.json"
    }

    private fun saveEverywhere(content: String, fileName: String) {
        // Location 1: next to DB (data/backups/)
        try {
            val appDir = File(System.getProperty("user.dir") ?: ".")
            val portableBackupDir = File(appDir, "data/backups")
            if (ensureDirWritable(portableBackupDir)) {
                File(portableBackupDir, fileName).writeText(content)
            }
        } catch (e: Exception) { e.printStackTrace() }

        // Location 2: D:\AdminFoodBackups\ (most important — survives C: failure)
        try {
            val dDrive = File("D:\\")
            if (dDrive.exists() && dDrive.canWrite()) {
                val dBackupDir = File(dDrive, "AdminFoodBackups")
                if (ensureDirWritable(dBackupDir)) {
                    File(dBackupDir, fileName).writeText(content)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }

        // Location 3: C:\Users\<user>\AdminFoodBackups (fallback if D: doesn't exist)
        try {
            val home = System.getProperty("user.home")
            if (home != null) {
                val homeBackupDir = File(home, "AdminFoodBackups")
                if (ensureDirWritable(homeBackupDir)) {
                    File(homeBackupDir, fileName).writeText(content)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun rotateOldBackups() {
        listOfNotNull(
            runCatching {
                File(File(System.getProperty("user.dir") ?: "."), "data/backups")
            }.getOrNull(),
            runCatching { File("D:\\AdminFoodBackups") }.getOrNull(),
            runCatching {
                System.getProperty("user.home")?.let { File(it, "AdminFoodBackups") }
            }.getOrNull()
        ).forEach { dir ->
            try {
                if (dir != null && dir.exists()) {
                    dir.listFiles()
                        ?.filter { it.name.startsWith(BACKUP_PREFIX) && it.extension == "json" }
                        ?.sortedByDescending { it.lastModified() }
                        ?.drop(MAX_BACKUPS_PER_LOCATION)
                        ?.forEach { it.delete() }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun ensureDirWritable(dir: File): Boolean {
        return try {
            if (!dir.exists()) dir.mkdirs()
            val test = File(dir, ".write_test")
            test.writeText("x")
            test.delete()
            true
        } catch (e: Exception) { false }
    }
}