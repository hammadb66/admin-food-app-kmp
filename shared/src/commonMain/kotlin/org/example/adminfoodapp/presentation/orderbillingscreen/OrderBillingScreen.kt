package org.example.adminfoodapp.presentation.orderbillingscreen

import GoldAccent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderBillingScreen(args: NavRoutes.OrderBillingScreen, navController: NavHostController) {
    val viewModel: OrderBillingViewModel = koinViewModel()
    val billItems by viewModel.billItems.collectAsState()
    val generalItem by viewModel.allGeneralItem.collectAsState()
    val showBatchVoidDialog by viewModel.showBatchVoidDialog.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerNumber by remember { mutableStateOf("") }
    val tableId = args.id
    val tableNumber = args.number
    val isTakeAway = args.isTakeAway

    val isCustomerNameValid = if (isTakeAway) customerName.isNotBlank() else true

    LaunchedEffect(Unit) {
        viewModel.checkoutSuccess.collect { navController.navigateUp() }
    }

    LaunchedEffect(tableId) {
        viewModel.loadTableOrder(tableId)
    }

    val grandTotal = billItems.sumOf { it.price * it.workingQuantity }

    // Unprinted Increments exist?
    val hasUnprintedIncrements = billItems.any { it.unprintedIncrement > 0 }

    // Pending Decrements exist? (Items reduced below printed baseline)
    val pendingDecrementsList = billItems.filter { it.pendingDecrement > 0 }
    val hasPendingDecrements = pendingDecrementsList.isNotEmpty()

    // Batch Void Dialog
    if (showBatchVoidDialog) {
        BatchVoidReasonDialog(
            decreasedItems = pendingDecrementsList,
            onConfirm = { reason -> viewModel.confirmBatchVoid(reason) },
            onDismiss = { viewModel.closeBatchVoidDialog() })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    if (isTakeAway) "Takeaway Order - Billing" else "Table ${args.number} - Billing",
                    fontWeight = FontWeight.Bold
                )
            },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.revertUnsavedChanges()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (hasPendingDecrements || hasUnprintedIncrements) {
                        TextButton(onClick = { viewModel.revertUnsavedChanges() }) {
                            Text("Revert Edits", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
        ) {
            // PENDING DECREMENTS TOP ALERT BANNER
            if (hasPendingDecrements) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "⚠️ Items Decreased / Deleted",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "${pendingDecrementsList.sumOf { it.pendingDecrement }} items pending cancellation audit log",
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = { viewModel.openBatchVoidDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Confirm & Reason")
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(if (isTakeAway) "Customer Name *" else "Customer Name (Optional)") },
                    isError = isTakeAway && customerName.isBlank(), // Error highlight if empty in Takeaway
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = customerNumber,
                    onValueChange = { customerNumber = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Customer Phone") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // MENU CATALOG (LEFT)
                Column(modifier = Modifier.weight(1.2f).fillMaxHeight()) {
                    Text(
                        "Quick Menu Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (generalItem is Resource.Success) {
                            val sampleMenuItems = (generalItem as Resource.Success<List<GenralItemsEntity>>).data
                            items(sampleMenuItems) { menu ->
                                Card(
                                    onClick = { viewModel.addItem(menu.id, menu.itemName, menu.price) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(menu.itemName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(
                                            "Rs. ${menu.price.toInt()}",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // RECEIPT & BILL (RIGHT)
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            "Current Table Receipt",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        if (billItems.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No items in bill", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(billItems, key = { it.menuItemId }) { item ->
                                    BillItemRow(
                                        item = item,
                                        onIncrease = { viewModel.addItem(item.menuItemId, item.itemName, item.price) },
                                        onDecrease = { viewModel.decreaseItem(item.menuItemId) },
                                        onDelete = { viewModel.deleteItem(item.menuItemId) })
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Rs. ${grandTotal.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // PRINT BUTTON (Active if Unprinted Increments or Drafts exist)
                        Button(
                            onClick = {
                                // 1. Pehle data ko OrderDetails mein badlo
                                val order = mapToOrderDetails(billItems, customerName, customerNumber, grandTotal)

                                // 2. Phir print karo
                                ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                                // 3. Phir ViewModel ko bolo save karne ke liye
                                viewModel.printAndSaveOrder(customerName, customerNumber, isTakeAway)


                            },
                            enabled = hasUnprintedIncrements && !hasPendingDecrements && isCustomerNameValid,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isTakeAway) "Print Takeaway KOT / Slip" else "Print Extra / Send KOT",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // CHECKOUT BUTTON
                        OutlinedButton(
                            onClick = {
                                // Final receipt print karo
                                val order = mapToOrderDetails(billItems, customerName, customerNumber, grandTotal)
                                ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)

                                // Checkout karo


                                viewModel.checkoutAndFreeTable(isTakeAway) },
                            enabled = billItems.isNotEmpty() && !hasUnprintedIncrements && !hasPendingDecrements && isCustomerNameValid,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isTakeAway) "Complete Takeaway Order" else "Checkout & Free Table",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BatchVoidReasonDialog(
    decreasedItems: List<BillItemUiState>, onConfirm: (reason: String) -> Unit, onDismiss: () -> Unit
) {
    var reasonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Item Reductions", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("The following printed items were reduced/deleted:")
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(decreasedItems) { item ->
                        Text(
                            "• ${item.itemName}: -${item.pendingDecrement} Qty",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason for Cancellation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reasonText) },
                enabled = reasonText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Save Void Log & Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        })
}

@Composable
fun BillItemRow(
    item: BillItemUiState, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                item.pendingDecrement > 0 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                item.unprintedIncrement > 0 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.itemName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))

                    // STATUS BADGES
                    if (item.pendingDecrement > 0) {
                        Text(
                            "[DECREASED -${item.pendingDecrement}]",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (item.unprintedIncrement > 0) {
                        Text(
                            "[UNPRINTED +${item.unprintedIncrement}]",
                            color = GoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text("[PRINTED]", color = Color.Gray, fontSize = 10.sp)
                    }
                }
                Text("Rs. ${item.price.toInt()} x ${item.workingQuantity}", fontSize = 12.sp, color = Color.DarkGray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                Text(
                    "${item.workingQuantity}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


// Ye helper function tumhari screen ke bilkul neeche rakh do
fun mapToOrderDetails(
    billItems: List<BillItemUiState>, customerName: String, customerNumber: String, grandTotal: Double
): OrderDetails {
    return OrderDetails(
        userName = customerName,
        phoneNumber = customerNumber,
        address = "N/A", // Agar address screen par nahi hai
        foodName = billItems.map { it.itemName },
        foodQuantity = billItems.map { it.workingQuantity },
        foodPrice = billItems.map { it.price.toString() },
        totalPrice = grandTotal.toString()
    )
}