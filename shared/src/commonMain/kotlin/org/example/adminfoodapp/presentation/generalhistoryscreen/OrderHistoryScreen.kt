package org.example.adminfoodapp.presentation.generalhistoryscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.OrderItemsEntity
import org.example.adminfoodapp.database.OrdersEntity
import org.example.adminfoodapp.database.VoidLogsEntity
import org.example.adminfoodapp.utils.ORDERSTATUS
import org.example.adminfoodapp.utils.ORDERTYPE
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

// Theme Colors for POS Audit
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryBlueBg = Color(0xFFEFF6FF)
private val RedError = Color(0xFFEF4444)
private val RedErrorBg = Color(0xFFFEF2F2)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val BlueReport = Color(0xFF1E40AF)
private val BlueReportBg = Color(0xFFEFF6FF)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BlueReportBg),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Delivery Summary Report",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueReport
                        )
                        Text(
                            text = "Print all orders",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = { viewModel.printBulkReport() },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueReport),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Print A4 Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            // Search Bar & Date Picker Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search TextField
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search name, phone, table...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = CardBorder
                    )
                )

                // Date Picker Action Chip
                Surface(
                    modifier = Modifier
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePickerDialog = true },
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(formattedDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // POS Segmented Tabs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE2E8F0)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    val isOrdersTab = uiState.selectedTab == 0

                    // Orders Tab Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.onTabSelected(0) },
                        color = if (isOrdersTab) Color.White else Color.Transparent,
                        shadowElevation = if (isOrdersTab) 2.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Orders (${uiState.orders.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isOrdersTab) PrimaryBlue else TextMuted
                            )
                        }
                    }

                    // Void Logs Tab Button
                    val isVoidTab = uiState.selectedTab == 1
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.onTabSelected(1) },
                        color = if (isVoidTab) Color.White else Color.Transparent,
                        shadowElevation = if (isVoidTab) 2.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Void Logs (${uiState.voidLogs.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isVoidTab) RedError else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Content List
            if (uiState.selectedTab == 0) {
                if (uiState.orders.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No orders found for this date", color = TextMuted, fontSize = 15.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.orders, key = { it.id }) { order ->
                            Box(modifier = Modifier.animateItem()) {
                                OrderHistoryCard(
                                    order = order,
                                    timeFormatted = timeFormat.format(Date(order.createdAt)),
                                    onClick = { viewModel.openOrderDetails(order) }
                                )
                            }
                        }
                    }
                }
            } else {
                if (uiState.voidLogs.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No cancellation logs found", color = TextMuted, fontSize = 15.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.voidLogs, key = { it.id }) { log ->
                            Box(modifier = Modifier.animateItem()) {
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
    }

    // Order Details Dialog Popup
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
                }) { Text("OK", fontWeight = FontWeight.Bold, color = PrimaryBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel", color = TextMuted) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// 1. ORDER HISTORY CARD ITEM
@Composable
fun OrderHistoryCard(
    order: OrdersEntity,
    timeFormatted: String,
    onClick: () -> Unit
) {
    val isDineIn = order.orderType == ORDERTYPE.DINEIN.id

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Type Badge + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isDineIn) PrimaryBlueBg else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDineIn) Icons.Default.TableBar else Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (isDineIn) PrimaryBlue else Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDineIn) "Table #${order.tableId ?: "-"}" else "Takeaway",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDineIn) PrimaryBlue else Color.DarkGray
                        )
                    }
                }

                Text(timeFormatted, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextMuted)
            }

            Spacer(Modifier.height(10.dp))

            // Main Info: Customer & Staff
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName.ifBlank { "Walk-in Customer" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (order.customerNumber.toString().isNotBlank()) order.customerNumber.toString() else "No Contact Info",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextMuted)
                        Spacer(Modifier.width(4.dp))
                        Text(order.createdByName, fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

            // Price & Details Trigger Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(order.orderStatus == ORDERSTATUS.PENDING.id) "Pending" else "Rs. ${order.totalAmount.toInt()}",
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 20.sp,
                    color = if (order.orderStatus == ORDERSTATUS.PENDING.id) RedError else TextColor

                )

                Text(
                    text = "View Details ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

// 2. VOID AUDIT LOG CARD ITEM
@Composable
fun VoidLogCard(
    log: VoidLogsEntity,
    timeFormatted: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RedErrorBg),
        border = BorderStroke(1.dp, Color(0xFFFECACA))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RedError, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = log.itemName,
                        fontWeight = FontWeight.Bold,
                        color = RedError,
                        fontSize = 15.sp
                    )
                }
                Text(timeFormatted, fontSize = 11.sp, color = TextMuted)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Qty Voided: -${log.cancelledQuantity}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Unit Price: Rs. ${log.unitPrice}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Reason: ${log.reason}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF991B1B)
            )

            Spacer(Modifier.height(8.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Voided By: ${log.voidedByName}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedError
                )
            }
        }
    }
}

// 3. FULL RECEIPT ORDER DETAIL DIALOG POPUP
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
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Customer: ${order.customerName.ifBlank { "Walk-in" }} (${order.customerNumber.toString().ifBlank { "N/A" }})", fontSize = 12.sp, color = Color.Black)
                        Text("Taken By Staff: ${order.createdByName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Time: $timeFormatted", fontSize = 11.sp, color = TextMuted)
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = CardBorder)

                Text("Ordered Items", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Spacer(Modifier.height(6.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.itemName} x${item.quantity}", fontSize = 12.sp, color = Color.Black, modifier = Modifier.weight(1f))
                            Text("Rs. ${item.price * item.quantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        }
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = CardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = if(order.orderStatus == ORDERSTATUS.PENDING.id) "Pending" else "Rs. ${order.totalAmount.toInt()}",
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 22.sp,
                        color = if (order.orderStatus == ORDERSTATUS.PENDING.id) RedError else TextColor
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPrintClick()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Re-Print Thermal Receipt", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}