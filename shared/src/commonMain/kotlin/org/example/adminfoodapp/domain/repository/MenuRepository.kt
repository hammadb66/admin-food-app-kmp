package org.example.adminfoodapp.domain.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.data.model.OrderDetails
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MenuRepository {
    fun getMenuItems(): Flow<Resource<MutableList<AllMenu>>>
    suspend fun addMenu(item: AllMenu, imageBytes: ByteArray): Result<Unit>
    suspend fun updateMenuItem(item: AllMenu, newImageBytes: ByteArray?): Result<Unit>

    fun getOrderHistory(): Flow<Resource<MutableList<OrderDetails>>>
    fun orderAccepted(orderDetails: OrderDetails): Flow<Resource<Unit>>
    fun orderDispatch(orderDetails: OrderDetails): Flow<Resource<Unit>>
    fun getCompleteOrder(): Flow<Resource<MutableList<OrderDetails>>>
    fun menuItemDelete(id: String): Flow<Resource<Unit>>
    fun cancelOrder(orderDetails: OrderDetails): Flow<Resource<Unit>>
    fun cancelConfirm(orderDetails: OrderDetails): Flow<Resource<Unit>>
}