package org.example.adminfoodapp.presentation.takeawayscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.database.repository.VoidLogBatchItem
import org.example.adminfoodapp.utils.DISCOUNTTYPE
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

class TakeAwayViewModel(
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
    private var activeOrderId: Long? = null
    private var orderObserveJob: kotlinx.coroutines.Job? = null
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerNumber = MutableStateFlow("")
    val customerNumber: StateFlow<String> = _customerNumber.asStateFlow()
    private val _discountType = MutableStateFlow(DISCOUNTTYPE.PERCENTAGE)
    val discountType: StateFlow<DISCOUNTTYPE> = _discountType.asStateFlow()

    private val _discountInput = MutableStateFlow("")
    val discountInput: StateFlow<String> = _discountInput.asStateFlow()

    init {
        getAllGeneralItem()
        loadOrder()
    }

    fun changeCustomerName(name: String) {
        _customerName.value = name
    }

    fun changeCustomerNumber(number: String) {
        _customerNumber.value = number
    }

    fun setDiscountType(type: DISCOUNTTYPE) {
        _discountType.value = type
        _discountInput.value = ""   // switch badalte hi purani value reset — warna confusion hoga
    }

    fun updateDiscountInput(value: String) {
        _discountInput.value = value
    }

    // Ye "dimaag" hai — subtotal lekar discount ka Rupee amount nikalta hai
    fun calculateDiscountAmount(subtotal: Double): Double {
        val rawValue = _discountInput.value.toDoubleOrNull() ?: 0.0
        return when (_discountType.value) {
            DISCOUNTTYPE.PERCENTAGE -> subtotal * (rawValue.coerceIn(0.0, 100.0) / 100.0)
            DISCOUNTTYPE.AMOUNT -> rawValue.coerceIn(0.0, subtotal)
        }
    }

    fun loadOrder() {
        viewModelScope.launch {
            val pendingOrder = repository.getPendingOrderForTakeAway()
            if (pendingOrder != null) {
                _customerName.value = pendingOrder.customerName
                _customerNumber.value = pendingOrder.customerNumber.toString()
                activeOrderId = pendingOrder.id
                observeCommittedItems(pendingOrder.id)
            }
        }
    }

    // FIX 1: Purana coroutine job cancel karna taake naye order mein masla na ho
    private fun observeCommittedItems(orderId: Long) {
        orderObserveJob?.cancel()

        orderObserveJob = viewModelScope.launch {
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


    // Ab resetAfterCheckout() use kar rahe hain taake Dine-In jaisa hi clean state mile.
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
                repository.processBatchVoid(
                    "Take Away[${_customerName.value}]", orderId, 0L, voidItems, reason, decreasedItemsMap
                )

                // UI filter karo (0 quantity wale nikal do)
                val updatedList = _billItems.value.filter { it.workingQuantity > 0 }
                _billItems.value = updatedList

                // Agar sab items khatam ho gaye toh bilkul fresh start lo
                if (updatedList.isEmpty()) {
                    orderObserveJob?.cancel()      // 👈 Purana Radio Band!
                    resetAfterCheckout()           // 👈 activeOrderId, name, number, discount — sab clean

                    // Agar aap chahte hain ke user usi screen par rahe taake naya order banaye,
                    // toh is _checkoutSuccess line ko comment rehne dein. Agar pichle screen par
                    // bhejna hai toh neeche wali line ko uncomment kar dein.
                    // _checkoutSuccess.emit(Unit)
                }
            }

            _showBatchVoidDialog.value = false
        }
    }


    fun printAndSaveOrder() {

        viewModelScope.launch {

            val orderType = "TAKE_AWAY"

            // 👇 STEP 1: Snapshot pehle hi jeb mein rakh lo — race se pehle
            val snapshot = _billItems.value
            val itemsToSave = snapshot.map { Triple(it.dbId, it.menuItemId, it.itemName) }
            val quantities = snapshot.associate { it.menuItemId to it.workingQuantity }
            val prices = snapshot.associate { it.menuItemId to it.price }

            if (activeOrderId == null) {
                activeOrderId = repository.createOrder(
                    customerName = _customerName.value.ifEmpty { "Walk-in Customer" },
                    customerNumber = if (_customerNumber.value.isEmpty()) 0L else _customerNumber.value.toLong(),
                    tableId = null,
                    orderType = ORDERTYPE.TAKEAWAY.id,
                    currentTimeStamp = System.currentTimeMillis()
                )
                observeCommittedItems(activeOrderId!!)   // ab isse itemsToSave pe asar nahi padega
            }

            val orderId = activeOrderId!!

            // Calculate Delta Items for Printing Slip (snapshot se, live state se nahi)
            val printList = snapshot.mapNotNull { item ->
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

            repository.commitAndPrintOrder(orderId, itemsToSave, quantities, prices)
        }
    }

    // Checkout
    fun checkoutAndFreeTable() {
        val orderId = activeOrderId ?: return
        println(activeOrderId)

        viewModelScope.launch {
            val subtotal = _billItems.value.sumOf { it.price * it.workingQuantity }
            val discountAmount = calculateDiscountAmount(subtotal)
            val finalTotal = subtotal - discountAmount

            repository.checkoutOrder(
                customerName = customerName.value,
                customerNumber = customerNumber.value,
                orderId = orderId,
                tableId = null,
                subtotal = subtotal,
                discountType = _discountType.value.id,
                discountValue = _discountInput.value.toDoubleOrNull() ?: 0.0,
                discountAmount = discountAmount,
                finalAmount = finalTotal
            )
            resetAfterCheckout()
            _checkoutSuccess.emit(Unit)
        }
    }

    private fun resetAfterCheckout() {
        _billItems.value = emptyList()
        _customerName.value = ""
        _customerNumber.value = ""
        _discountType.value = DISCOUNTTYPE.PERCENTAGE   // 👈 naya
        _discountInput.value = ""                        // 👈 naya
        activeOrderId = null
    }

    private fun getAllGeneralItem() {
        viewModelScope.launch {
            repository.getAllMenuItems().collect { items ->
                _allGeneralItem.value = items
            }
        }
    }
}