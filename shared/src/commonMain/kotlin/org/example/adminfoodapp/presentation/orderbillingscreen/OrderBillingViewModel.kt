package org.example.adminfoodapp.presentation.orderbillingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository

class OrderBillingViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _orderItems = MutableStateFlow<List<OrderItemsEntity>>(emptyList())
    val orderItems: StateFlow<List<OrderItemsEntity>> = _orderItems.asStateFlow()

    private val _checkoutSuccess = MutableSharedFlow<Unit>()
    val checkoutSuccess: SharedFlow<Unit> = _checkoutSuccess.asSharedFlow()

    private var currentTableId: Long = 0L
    private var currentOrderId: Long = 0L

    // 1. Table ka active order load karein (agar order na ho toh DB me create karein)
    fun loadTableOrder(tableId: Long) {
        currentTableId = tableId
        viewModelScope.launch {
            val currentTimeStamp = System.currentTimeMillis()

            // Database se Active Order ID mangwayein
            currentOrderId = repository.getOrCreatePendingOrder(tableId, currentTimeStamp)

            // Live Flow: Jaise hi DB me items update honge, UI auto refresh ho jayegi
            repository.getOrderItems(currentOrderId).collect { items ->
                _orderItems.value = items
            }
        }
    }

    // 2. Menu Item ko Bill me Add Karein
    fun addItemToOrder(menuItemId: Long, itemName: String, price: Double) {
        if (currentOrderId == 0L) return
        viewModelScope.launch {
            // Check karta hai: Agar item pehle se bill me hai toh Quantity + 1 karega,
            // varna naya row add karega
            repository.addOrIncrementItem(
                orderId = currentOrderId,
                menuItemId = menuItemId,
                itemName = itemName,
                price = price
            )
        }
    }

    // 3. Quantity (+1 ya -1) Change Karein
    fun updateQuantity(item: OrderItemsEntity, delta: Int) {
        viewModelScope.launch {
            val newQuantity = item.quantity + delta
            // Agar quantity 0 ho jaye toh repository isko automatically DELETE kar degi
            repository.updateQuantity(item.id, newQuantity)
        }
    }

    // 4. Direct Delete Karein
    fun removeItem(item: OrderItemsEntity) {
        viewModelScope.launch {
            repository.removeItem(item.id)
        }
    }

    // 5. Checkout & Table Free Karein
    fun checkoutAndPrintBill() {
        if (currentOrderId == 0L) return
        viewModelScope.launch {
            val totalAmount = _orderItems.value.sumOf { it.price * it.quantity }
            repository.checkoutOrder(currentOrderId, currentTableId, totalAmount)
            _checkoutSuccess.emit(Unit)
        }
    }


    fun addNewMenuItem(name: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.addMenuItem(name, price, category)
        }
    }
}