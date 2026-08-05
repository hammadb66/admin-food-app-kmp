package org.example.adminfoodapp.presentation.orderbillingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.database.repository.VoidLogBatchItem
import org.example.adminfoodapp.utils.ORDERTYPE
import org.example.adminfoodapp.utils.Resource

// Unified Item UI State
data class BillItemUiState(
    val dbId: Long? = null,           // Null for new drafts
    val menuItemId: Long,
    val itemName: String,
    val price: Double,
    val printedQuantity: Int = 0,     // Saved Baseline in DB
    var workingQuantity: Int = 1      // Current UI working quantity
) {
    val unprintedIncrement: Int
        get() = if (workingQuantity > printedQuantity) workingQuantity - printedQuantity else 0

    val pendingDecrement: Int
        get() = if (workingQuantity < printedQuantity) printedQuantity - workingQuantity else 0
}

class OrderBillingViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _billItems = MutableStateFlow<List<BillItemUiState>>(emptyList())
    val billItems: StateFlow<List<BillItemUiState>> = _billItems.asStateFlow()

    private val _showBatchVoidDialog = MutableStateFlow(false)
    val showBatchVoidDialog: StateFlow<Boolean> = _showBatchVoidDialog.asStateFlow()
    private val _checkoutSuccess = MutableSharedFlow<Unit>()
    val checkoutSuccess: SharedFlow<Unit> = _checkoutSuccess.asSharedFlow()

    private val _allGeneralItem = MutableStateFlow<Resource<List<GenralItemsEntity>>>(Resource.Loading)
    val allGeneralItem = _allGeneralItem.asStateFlow()

    private var currentTableId: Long = 0L
    private var activeOrderId: Long? = null

    init {
        getAllGeneralItem()
    }

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerNumber = MutableStateFlow("")
    val customerNumber: StateFlow<String> = _customerNumber.asStateFlow()

    fun changeCustomerName(name: String) {
        _customerName.value = name
    }

    fun changeCustomerNumber(number: String) {
        _customerNumber.value = number
    }

    fun loadTableOrder(tableId: Long) {
        currentTableId = tableId
        viewModelScope.launch {
            val pendingOrder = repository.getPendingOrderForTable(tableId)
            if (pendingOrder != null) {
                _customerName.value = pendingOrder.customerName
                _customerNumber.value = pendingOrder.customerNumber.toString()
                activeOrderId = pendingOrder.id
                observeCommittedItems(pendingOrder.id)
            }
        }
    }

    private fun observeCommittedItems(orderId: Long) {
        viewModelScope.launch {
            repository.getOrderItems(orderId).collect { dbList ->
                val currentWorkingMap = _billItems.value.associateBy { it.menuItemId }
                _billItems.value = dbList.map { db ->
                    val working = currentWorkingMap[db.menuItemId]?.workingQuantity ?: db.quantity.toInt()
                    BillItemUiState(
                        dbId = db.id,
                        menuItemId = db.menuItemId,
                        itemName = db.itemName,
                        price = db.price,
                        printedQuantity = db.printedQuantity.toInt(),
                        workingQuantity = working
                    )
                }
            }
        }
    }

    // Add / Increment Item
    fun addItem(menuItemId: Long, itemName: String, price: Double) {
        val list = _billItems.value.toMutableList()
        val index = list.indexOfFirst { it.menuItemId == menuItemId }

        if (index != -1) {
            list[index] = list[index].copy(workingQuantity = list[index].workingQuantity + 1)
        } else {
            list.add(BillItemUiState(menuItemId = menuItemId, itemName = itemName, price = price, workingQuantity = 1))
        }
        _billItems.value = list
    }

    // Decrease Quantity (Can restore increments down to printedQuantity without triggering void)
    fun decreaseItem(menuItemId: Long) {
        val list = _billItems.value.toMutableList()
        val index = list.indexOfFirst { it.menuItemId == menuItemId }
        if (index != -1) {
            val item = list[index]
            val newQty = item.workingQuantity - 1
            if (newQty <= 0 && item.dbId == null) {
                // Draft items remove completely
                list.removeAt(index)
            } else {
                list[index] = item.copy(workingQuantity = maxOf(0, newQty))
            }
            _billItems.value = list
        }
    }

    // Explicit Delete Button Click
    fun deleteItem(menuItemId: Long) {
        val list = _billItems.value.toMutableList()
        val index = list.indexOfFirst { it.menuItemId == menuItemId }
        if (index != -1) {
            val item = list[index]
            if (item.dbId == null) {
                list.removeAt(index)
            } else {
                // Set working qty to 0 (Queued for void confirmation)
                list[index] = item.copy(workingQuantity = 0)
            }
            _billItems.value = list
        }
    }

    // Revert Unsaved Edits (Called on Back or Revert Button)
    fun revertUnsavedChanges() {
        val list = _billItems.value.mapNotNull { item ->
            if (item.dbId == null) null // Remove unprinted drafts
            else item.copy(workingQuantity = item.printedQuantity) // Restore to printed baseline
        }
        _billItems.value = list
    }

    // Open/Close Batch Void Modal
    fun openBatchVoidDialog() {
        _showBatchVoidDialog.value = true
    }

    fun closeBatchVoidDialog() {
        _showBatchVoidDialog.value = false
    }

    // Confirm Batch Decrement / Cancellation Reason
// Confirm Batch Decrement / Cancellation Reason
    fun confirmBatchVoid(reason: String) {
        val orderId = activeOrderId ?: return
        viewModelScope.launch {
            val voidItems = mutableListOf<VoidLogBatchItem>()
            val decreasedItemsMap = mutableMapOf<Long, Int>()

            _billItems.value.forEach { item ->
                if (item.pendingDecrement > 0 && item.dbId != null) {
                    voidItems.add(
                        VoidLogBatchItem(
                            orderId = orderId,
                            itemName = item.itemName,
                            cancelledQuantity = item.pendingDecrement,
                            unitPrice = item.price
                        )
                    )
                    decreasedItemsMap[item.dbId] = item.workingQuantity
                }
            }

            if (voidItems.isNotEmpty()) {
                // 👈 1. Pass currentTableId to repository
                repository.processBatchVoid(orderId, currentTableId, voidItems, reason, decreasedItemsMap)

                // 👈 2. Check if all items in UI were deleted (workingQuantity == 0)
                val remainingItemsCount = _billItems.value.count { it.workingQuantity > 0 }
                if (remainingItemsCount == 0) {
                    activeOrderId = null
                    // Screen ko auto navigate back / finish kar do
                    _checkoutSuccess.emit(Unit)
                }
            }

            _showBatchVoidDialog.value = false
        }
    }

    // Print & Save (Prints Increments & New Drafts together)
// Print & Save
    fun printAndSaveOrder(isTakeAway: Boolean) {

        viewModelScope.launch {

            val orderType = "DINE_IN"

            println(activeOrderId)
            if (activeOrderId == null) {
                activeOrderId = repository.createOrder(
                    customerName = _customerName.value.ifEmpty { "Walk-in Customer" },
                    customerNumber = if (_customerNumber.value.isEmpty()) 0L else _customerNumber.value.toLong(),
                    tableId = if (isTakeAway) null else currentTableId,
                    orderType = ORDERTYPE.DINEIN.id,
                    currentTimeStamp = System.currentTimeMillis()
                )
                observeCommittedItems(activeOrderId!!)
            }

            val orderId = activeOrderId!!

            // Calculate Delta Items for Printing Slip
            val printList = _billItems.value.mapNotNull { item ->
                val delta = item.unprintedIncrement
                if (delta > 0) {
                    "[$orderType] ITEM: ${item.itemName} | QTY: +$delta | PRICE: Rs. ${item.price}"
                } else null
            }

            if (printList.isNotEmpty()) {
                println("====== PRINTING $orderType RECEIPT SLIP ======")
                printList.forEach { println(it) }
                println("==========================================")
            }

            val itemsToSave = _billItems.value.map { Triple(it.dbId, it.menuItemId, it.itemName) }
            val quantities = _billItems.value.associate { it.menuItemId to it.workingQuantity }
            val prices = _billItems.value.associate { it.menuItemId to it.price }

            repository.commitAndPrintOrder(orderId, itemsToSave, quantities, prices)
        }
    }

    // Checkout
    fun checkoutAndFreeTable(isTakeAway: Boolean) {
        val orderId = activeOrderId ?: return
        println(activeOrderId)

        viewModelScope.launch {
            val total = _billItems.value.sumOf { it.price * it.workingQuantity }
            repository.checkoutOrder(
                customerName = customerName.value,
                customerNumber = customerNumber.value,
                orderId = orderId,
                tableId = if (isTakeAway) null else currentTableId,
                finalAmount = total
            )
            // 3️⃣ UI‑side reset
            resetAfterCheckout()
            // 4️⃣ Notify UI (navigate back)
            _checkoutSuccess.emit(Unit)
        }
    }

    /** Reset all UI‑state after a successful checkout */
    private fun resetAfterCheckout() {
        // UI list ko khali karo
        _billItems.value = emptyList()
        // Customer info reset (optional – agar aap chahte ho)
        _customerName.value = ""
        _customerNumber.value = ""
        // Active order id ko null set karo – next time naya order create hoga
        activeOrderId = null
        // Table‑specific data (take‑away ke liye tableId = 0) ko bhi clean kar dein
        currentTableId = 0L
    }

    private fun getAllGeneralItem() {
        viewModelScope.launch {
            repository.getAllMenuItems().collect { items ->
                _allGeneralItem.value = items
            }
        }
    }
}