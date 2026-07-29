package org.example.adminfoodapp.database.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.database.model.AllMenu
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.remote.FirebaseMenuDataSource
import org.example.adminfoodapp.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow

class MenuRepositoryImpl(
    private val dataSource: FirebaseMenuDataSource
) : MenuRepository {


    override fun getMenuItems(): Flow<Resource<MutableList<AllMenu>>> = dataSource.getMenuItems()

    override suspend fun addMenu(
        item: AllMenu, imageBytes: ByteArray
    ): Result<Unit> = dataSource.addMenu(item, imageBytes)

    override suspend fun updateMenuItem(
        item: AllMenu, newImageBytes: ByteArray?
    ): Result<Unit> = dataSource.updateMenu(item, newImageBytes)

    override fun getOrderHistory(): Flow<Resource<MutableList<OrderDetails>>> = dataSource.getOrderHistory()

    override fun orderAccepted(orderDetails: OrderDetails): Flow<Resource<Unit>> =
        dataSource.orderAccepted(orderDetails)

    override fun orderDispatch(orderDetails: OrderDetails): Flow<Resource<Unit>> =
        dataSource.orderDispatch(orderDetails)

    override fun getCompleteOrder(): Flow<Resource<MutableList<OrderDetails>>> = dataSource.getCompleteOrder()

    override fun menuItemDelete(id: String) = dataSource.menuItemDelete(id)

    override fun cancelOrder(orderDetails: OrderDetails) = dataSource.cancelOrder(orderDetails)

    override fun cancelConfirm(orderDetails: OrderDetails) = dataSource.cancelConfirm(orderDetails)
}