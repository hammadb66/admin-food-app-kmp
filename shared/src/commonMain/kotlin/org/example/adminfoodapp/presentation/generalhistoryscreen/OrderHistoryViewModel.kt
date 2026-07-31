package org.example.adminfoodapp.presentation.generalhistoryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.OrdersEntity
import org.example.adminfoodapp.database.VoidLogsEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository
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
        println("=== SINGLE ORDER RECEIPT ===")
        println("Order ID: #${order.id} | Staff: ${order.createdByName}")
        println("Customer: ${order.customerName}")
        items.forEach { println("${it.itemName} x ${it.quantity} = Rs. ${it.price * it.quantity}") }
        println("Total: Rs. ${order.totalAmount}")
    }

    // 3. TOP GLOBAL PRINT (Bulk Summary Print)
    fun printBulkReport() {
        val currentState = _uiState.value
        if (currentState.selectedTab == 0) {
            // ALL ORDERS DAILY REPORT
            println("====== ALL ORDERS DAILY REPORT ======")
            println("Total Orders Count: ${currentState.orders.size}")
            var grandTotal = 0.0
            currentState.orders.forEach { order ->
                grandTotal += order.totalAmount
                println("#${order.id} | ${order.orderType} | Rs.${order.totalAmount} | Taken By: ${order.createdByName}")
            }
            println("GRAND TOTAL SALES: Rs. $grandTotal")
            println("====================================")
        } else {
            // ALL VOID LOGS DAILY REPORT
            println("====== ALL VOID AUDIT REPORT ======")
            println("Total Voided Items: ${currentState.voidLogs.size}")
            currentState.voidLogs.forEach { voidLog ->
                println("Item: ${voidLog.itemName} | Qty: ${voidLog.cancelledQuantity} | Reason: ${voidLog.reason} | Voided By: ${voidLog.voidedByName}")
            }
            println("===================================")
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