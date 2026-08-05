package org.example.adminfoodapp.presentation.generalhistoryscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.OrdersEntity
import org.example.adminfoodapp.database.VoidLogsEntity
import org.example.adminfoodapp.utils.ORDERTYPE
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavHostController) {
    val viewModel: OrderHistoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.ENGLISH) }
    val formattedDate = remember(uiState.selectedDateInMillis) {
        dateFormat.format(Date(uiState.selectedDateInMillis))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History & Audit Logs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // TOP BAR GLOBAL PRINT BUTTON (Print All Orders / All Void Logs)
                    IconButton(onClick = { viewModel.printBulkReport() }) {
                        Icon(Icons.Default.Print, contentDescription = "Print All Report", tint = MaterialTheme.colorScheme.primary)
                    }

                    // Date Select Button
                    OutlinedButton(
                        onClick = { showDatePickerDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(formattedDate, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // SEARCH BAR
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Name, Phone, or Table #...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // TABS
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("Orders History (${uiState.orders.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("Void Logs (${uiState.voidLogs.size})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // LIST DISPLAY
            if (uiState.selectedTab == 0) {
                if (uiState.orders.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.orders, key = { it.id }) { order ->
                            OrderHistoryCard(
                                order = order,
                                timeFormatted = timeFormat.format(Date(order.createdAt)),
                                onClick = { viewModel.openOrderDetails(order) } // 👈 Click to view Details
                            )
                        }
                    }
                }
            } else {
                if (uiState.voidLogs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No cancellation logs found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.voidLogs, key = { it.id }) { log ->
                            VoidLogCard(
                                log = log,
                                timeFormatted = timeFormat.format(Date(log.createdAt))
                            )
                        }
                    }
                }
            }
        }
    }

    // ORDER DETAILS DIALOG POPUP (Poori History + Single Print Button)
    uiState.selectedOrderForDetail?.let { selectedOrder ->
        OrderDetailDialog(
            order = selectedOrder,
            items = uiState.selectedOrderItems,
            timeFormatted = timeFormat.format(Date(selectedOrder.createdAt)),
            onDismiss = { viewModel.closeOrderDetails() },
            onPrintClick = {
                viewModel.printSingleOrder(selectedOrder, uiState.selectedOrderItems)
            }
        )
    }

    // Date Picker Dialog
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDateInMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDateSelected(it) }
                    showDatePickerDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// 1. ORDER CARD ITEM
@Composable
fun OrderHistoryCard(
    order: OrdersEntity,
    timeFormatted: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // 👈 Card click triggers detail popup
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (order.orderType == ORDERTYPE.DINEIN.id) "Table #${order.tableId} (Dine-In)" else "Takeaway Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(timeFormatted, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(6.dp))

            Text("Customer: ${order.customerName.ifBlank { "Walk-in" }} (${order.customerNumber})", fontSize = 13.sp)

            // STAFF NAME DISPLAY
            Text("Order Taken By: ${order.createdByName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: Rs. ${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Text("Tap to view details ➔", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// 2. VOID AUDIT CARD ITEM
@Composable
fun VoidLogCard(
    log: VoidLogsEntity,
    timeFormatted: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚠️ Item: ${log.itemName}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text(timeFormatted, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(4.dp))

            Text("Cancelled Qty: -${log.cancelledQuantity} | Unit Price: Rs. ${log.unitPrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Reason: ${log.reason}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)

            // VOIDED BY STAFF DISPLAY
            Text("Voided By Staff: ${log.voidedByName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        }
    }
}

// 3. FULL ORDER DETAIL DIALOG POPUP
@Composable
fun OrderDetailDialog(
    order: OrdersEntity,
    items: List<OrderItemsEntity>,
    timeFormatted: String,
    onDismiss: () -> Unit,
    onPrintClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order Details #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                order.tableId?.let { Text("Type: ${order.orderType} ${if(it > 0) "| Table #${order.tableId}" else ""}", fontWeight = FontWeight.Medium) }
                Text("Customer: ${order.customerName} (${order.customerNumber})", fontSize = 13.sp)
                Text("Taken By: ${order.createdByName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Time: $timeFormatted", fontSize = 12.sp, color = Color.Gray)

                Divider(Modifier.padding(vertical = 10.dp))

                Text("Ordered Items:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))

                // ITEMS LIST IN RECEIPT STYLE
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.itemName} x${item.quantity}", fontSize = 13.sp)
                            Text("Rs. ${item.price * item.quantity}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Divider(Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Rs. ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPrintClick()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Confirm & Re-Print Receipt")
            }
        }
    )
}