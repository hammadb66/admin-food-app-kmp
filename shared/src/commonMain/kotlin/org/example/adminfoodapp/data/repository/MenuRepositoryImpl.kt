package org.example.adminfoodapp.data.repository

import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.data.model.OrderDetails
import org.example.adminfoodapp.data.remote.FirebaseMenuDataSource
import org.example.adminfoodapp.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class MenuRepositoryImpl(
    private val dataSource: FirebaseMenuDataSource
) : MenuRepository {

    override suspend fun addMenu(item: AllMenu, file: File): Result<Unit> =
        dataSource.addMenu(item, file)

    override fun getMenuItems(): Flow<Resource<MutableList<AllMenu>>> =
        dataSource.getMenuItems()

    override fun getOrderHistory(): Flow<Resource<MutableList<OrderDetails>>> =
        dataSource.getOrderHistory()

    override fun orderAccepted(orderDetails: OrderDetails): Flow<Resource<Unit>> =
        dataSource.orderAccepted(orderDetails)

    override fun orderDispatch(orderDetails: OrderDetails): Flow<Resource<Unit>> =
        dataSource.orderDispatch(orderDetails)

    override fun getCompleteOrder(): Flow<Resource<MutableList<OrderDetails>>> =
        dataSource.getCompleteOrder()

    override fun menuItemDelete(id: String) = dataSource.menuItemDelete(id)

    override suspend fun updateMenuItem(item: AllMenu, newFile: File?) =
        dataSource.updateMenu(item, newFile)

    override fun cancelOrder(orderDetails: OrderDetails) = dataSource.cancelOrder(orderDetails)

    override fun cancelConfirm(orderDetails: OrderDetails) = dataSource.cancelConfirm(orderDetails)
}