package org.example.adminfoodapp.presentation.generalhistoryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.OrdersEntity
import org.example.adminfoodapp.database.VoidLogsEntity
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import java.util.Calendar

data class HistoryUiState(
    val orders: List<OrdersEntity> = emptyList(),
    val voidLogs: List<VoidLogsEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedDateInMillis: Long = System.currentTimeMillis(),
    val selectedTab: Int = 0, // 0 = Orders, 1 = Void
    val selectedOrderForDetail: OrdersEntity? = null, // Detail Dialog ke liye
    val selectedOrderItems: List<OrderItemsEntity> = emptyList() // Order items
)

class OrderHistoryViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()


    init {
        loadHistoryData()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadHistoryData()
    }

    fun onDateSelected(dateInMillis: Long) {
        _uiState.update { it.copy(selectedDateInMillis = dateInMillis) }
        loadHistoryData()
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    // 1. Order Detail Dialog Open Karein
    fun openOrderDetails(order: OrdersEntity) {
        viewModelScope.launch {
            val items = repository.getOrderItemsForReprint(order.id)
            _uiState.update {
                it.copy(selectedOrderForDetail = order, selectedOrderItems = items)
            }
        }
    }

    fun closeOrderDetails() {
        _uiState.update { it.copy(selectedOrderForDetail = null, selectedOrderItems = emptyList()) }
    }

    private fun loadHistoryData() {
        val (startOfDay, endOfDay) = getStartAndEndOfDay(_uiState.value.selectedDateInMillis)
        val query = _uiState.value.searchQuery

        viewModelScope.launch {
            repository.getOrdersHistory(startOfDay, endOfDay, query).collect { list ->
                _uiState.update { it.copy(orders = list) }
            }
        }

        viewModelScope.launch {
            repository.getVoidLogsHistory(startOfDay, endOfDay).collect { voidList ->
                _uiState.update { it.copy(voidLogs = voidList) }
            }
        }
    }

    // 2. Single Order Bill Print
    fun printSingleOrder(order: OrdersEntity, items: List<OrderItemsEntity>) {
        val orderDetails = OrderDetails(
            userName = order.customerName,
            phoneNumber = order.customerNumber.toString(),
            address = "N/A",
            foodName = items.map { it.itemName },
            foodQuantity = items.map { it.quantity.toInt() },
            foodPrice = items.map { it.price.toString() },
            totalPrice = order.totalAmount.toString()
        )

        ReceiptPrinter.printReceipt(orderDetails, PrintMode.A4)
    }

    fun printBulkReport() {
        val currentState = _uiState.value

        if (currentState.selectedTab == 0) {
            // 0 = Normal Orders Tab
            val currentOrders = currentState.orders

            if (currentOrders.isNotEmpty()) {
                val printableOrders = currentOrders.map { order ->
                    val orderItems = orderItems(order.id)
                    OrderDetails(
                        userName = order.customerName,
                        totalPrice = order.totalAmount.toString(),
                        phoneNumber = order.customerNumber.toString(),
                        address = "",
                        foodName = orderItems.map { it.itemName },
                        foodQuantity = orderItems.map { it.printedQuantity.toInt() },
                        foodPrice = orderItems.map { it.price.toString() },
                    )
                }
                ReceiptPrinter.printAllOrders(printableOrders,"Completed Order History")
            }
        } else {
            // 1 = Void Orders Tab
            val currentVoidLogs = currentState.voidLogs

            if (currentVoidLogs.isNotEmpty()) {
                val printableVoidOrders = currentVoidLogs.map { voidLog ->
                    val list = orderItems(voidLog.orderId)
                    OrderDetails(
                        userName = voidLog.voidedByName,
                        totalPrice = voidLog.unitPrice.toString(),
                        phoneNumber = voidLog.reason,
                        address = "",
                        foodName = list.map { it.itemName },
                        foodQuantity = list.map { it.quantity.toInt() },
                        foodPrice = list.map { it.price.toString() },
                    )
                }
                ReceiptPrinter.printAllOrders(printableVoidOrders,"Cancels Order History")
            }
        }
    }


    fun orderItems(id: Long): List<OrderItemsEntity> {
        return runBlocking {
            repository.getOrderItemsForReprint(id)
        }
    }

    private fun getStartAndEndOfDay(millis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return Pair(startOfDay, calendar.timeInMillis)
    }
}