package org.example.adminfoodapp.database.backup

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseBackup(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val tables: List<TableBackup> = emptyList(),
    val menuItems: List<MenuItemBackup> = emptyList(),
    val orders: List<OrderBackup> = emptyList(),
    val orderItems: List<OrderItemBackup> = emptyList(),
    val voidLogs: List<VoidLogBackup> = emptyList(),
    val users: List<UserBackup> = emptyList()
)

@Serializable
data class TableBackup(
    val id: Long, val tableNumber: String, val capacity: Long, val status: Long
)

@Serializable
data class MenuItemBackup(
    val id: Long, val itemName: String, val price: Double, val category: String
)

@Serializable
data class OrderBackup(
    val id: Long, val tableId: Long?, val customerName: String, val customerNumber: Long,
    val orderType: Long, val subtotal: Double, val discountType: Long, val discountValue: Double,
    val discountAmount: Double, val totalAmount: Double, val orderStatus: Long, val createdAt: Long,
    val createdByID: Long, val createdByName: String
)

@Serializable
data class OrderItemBackup(
    val id: Long, val orderId: Long, val menuItemId: Long, val itemName: String,
    val quantity: Long, val printedQuantity: Long, val price: Double
)

@Serializable
data class VoidLogBackup(
    val id: Long, val orderId: Long, val itemName: String, val cancelledQuantity: Long,
    val unitPrice: Double, val reason: String, val createdAt: Long,
    val voidByID: Long, val voidedByName: String, val customerName: String
)

@Serializable
data class UserBackup(
    val id: Long, val name: String, val password: String,
    val isAdmin: Long, val userAccess: Long
)