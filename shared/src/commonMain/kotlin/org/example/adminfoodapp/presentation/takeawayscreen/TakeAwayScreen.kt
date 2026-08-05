package org.example.adminfoodapp.presentation.takeawayscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

// --- MODERN PRO POS COLOR PALETTE ---
private val SurfaceBackground = Color(0xFFF8FAFC)
private val CardBackground = Color(0xFFFFFFFF)
private val PrimaryAccent = Color(0xFF059669)
private val PrimaryAccentLight = Color(0xFFECFDF5)
private val GoldAccent = Color(0xFFD97706)
private val GoldAccentLight = Color(0xFFFEF3C7)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)

@Composable
fun TakeAwayScreen(navController: NavHostController) {
    val viewModel: TakeAwayViewModel = koinViewModel()
    val billItems by viewModel.billItems.collectAsState()
    val generalItem by viewModel.allGeneralItem.collectAsState()
    val showBatchVoidDialog by viewModel.showBatchVoidDialog.collectAsState()

    val customerName by viewModel.customerName.collectAsState()
    val customerNumber by viewModel.customerNumber.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.checkoutSuccess.collect { navController.navigateUp() }
    }

    val isCustomerNameValid = customerName.isNotBlank()

    val grandTotal = billItems.sumOf { it.price * it.workingQuantity }
    val hasUnprintedIncrements = billItems.any { it.unprintedIncrement > 0 }
    val pendingDecrementsList = billItems.filter { it.pendingDecrement > 0 }
    val hasPendingDecrements = pendingDecrementsList.isNotEmpty()

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryAccentLight,
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableRestaurant,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.padding(6.dp).size(18.dp)
                        )
                    }
                    Column {
                        Text( text = "Takeaway POS Terminal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Quick Order Entry & Thermal Checkout", fontSize = 10.sp, color = TextSecondary
                        )
                    }
                }
            },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.revertUnsavedChanges()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    if (hasPendingDecrements || hasUnprintedIncrements) {
                        OutlinedButton(
                            onClick = { viewModel.revertUnsavedChanges() },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Revert", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBackground),
                modifier = Modifier.shadow(2.dp)
            )
        }, containerColor = SurfaceBackground
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            val isCompactScreen = maxWidth < 700.dp

            Column(
                modifier = Modifier.fillMaxSize()
                    .then(if (isCompactScreen) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(12.dp)
            ) {
                // PENDING DECREMENTS ALERT BANNER
                if (hasPendingDecrements) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Items Decreased / Deleted",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "${pendingDecrementsList.sumOf { it.pendingDecrement }} item(s) pending cancellation reason",
                                        fontSize = 10.sp,
                                        color = Color(0xFF7F1D1D)
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.openBatchVoidDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // CUSTOMER INFORMATION CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorderColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { viewModel.changeCustomerName(it) },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text(text = "Customer Name *" , fontSize = 11.sp
                                )
                            },
                            isError =customerName.isBlank(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardBorderColor
                            )
                        )
                        OutlinedTextField(
                            value = customerNumber,
                            onValueChange = { viewModel.changeCustomerNumber(it) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Customer Phone", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardBorderColor
                            )
                        )
                    }
                }

                // RESPONSIVE LAYOUT SWITCHING
                if (isCompactScreen) {
                    Column(
                        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuCatalogSection(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            generalItem = generalItem,
                            onAddItem = { id, name, price -> viewModel.addItem(id, name, price) },
                            modifier = Modifier.fillMaxWidth().height(260.dp)
                        )
                        ReceiptSection(
                            billItems = billItems,
                            grandTotal = grandTotal,
                            hasUnprintedIncrements = hasUnprintedIncrements,
                            hasPendingDecrements = hasPendingDecrements,
                            isCustomerNameValid = isCustomerNameValid,
                            customerName = customerName,
                            customerNumber = customerNumber,
                            viewModel = viewModel,
                            isCompact = true,
                            modifier = Modifier.fillMaxWidth().height(360.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        MenuCatalogSection(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            generalItem = generalItem,
                            onAddItem = { id, name, price -> viewModel.addItem(id, name, price) },
                            modifier = Modifier.weight(1.2f).fillMaxHeight()
                        )
                        ReceiptSection(
                            billItems = billItems,
                            grandTotal = grandTotal,
                            hasUnprintedIncrements = hasUnprintedIncrements,
                            hasPendingDecrements = hasPendingDecrements,
                            isCustomerNameValid = isCustomerNameValid,
                            customerName = customerName,
                            customerNumber = customerNumber,
                            viewModel = viewModel,
                            isCompact = false,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCatalogSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    generalItem: Resource<List<GenralItemsEntity>>,
    onAddItem: (Long, String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quick Menu Catalog", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Select item to add", fontSize = 10.sp, color = TextSecondary)
        }

        Spacer(Modifier.height(6.dp))

        // FIXED UNCLIPPED SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search menu item...", fontSize = 12.sp, color = TextSecondary) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PrimaryAccent,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = CardBorderColor,
            )
        )

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (generalItem is Resource.Success) {
                val menuList = (generalItem).data
                val filteredList = if (searchQuery.isBlank()) {
                    menuList
                } else {
                    menuList.filter { it.itemName.contains(searchQuery, ignoreCase = true) }
                }

                items(filteredList) { menu ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .clickable { onAddItem(menu.id, menu.itemName, menu.price) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = PrimaryAccentLight, shape = CircleShape, modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.RestaurantMenu,
                                        contentDescription = null,
                                        tint = PrimaryAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                menu.itemName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Rs. ${menu.price.toInt()}",
                                color = PrimaryAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptSection(
    billItems: List<BillItemUiState>,
    grandTotal: Double,
    hasUnprintedIncrements: Boolean,
    hasPendingDecrements: Boolean,
    isCustomerNameValid: Boolean,
    customerName: String,
    customerNumber: String,
    viewModel: TakeAwayViewModel,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorderColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Current Table Receipt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                    )
                }
                Surface(
                    color = PrimaryAccentLight, shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${billItems.size} Items",
                        color = PrimaryAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorderColor)

            // SELECTED ITEMS LIST WITH FULL HEIGHT FIX
            if (billItems.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("No items added to receipt yet", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorderColor)

            // GRAND TOTAL SECTION
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text("Grand Total", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Text("Incl. taxes", fontSize = 9.sp, color = TextSecondary)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Rs. ${grandTotal.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // PRINT BUTTON
            Button(
                onClick = {
                    val order = mapToOrderDetails(billItems, customerName, customerNumber, grandTotal)
                    println(order)
                    ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                    viewModel.printAndSaveOrder()
                },
                enabled = hasUnprintedIncrements && !hasPendingDecrements && isCustomerNameValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent, disabledContainerColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth().height(38.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                   text= "Print Takeaway KOT / Slip",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // CHECKOUT BUTTON
            Button(
                onClick = {
                    val order = mapToOrderDetails(billItems, customerName, customerNumber, grandTotal)
                    ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                    viewModel.checkoutAndFreeTable()
                },
                enabled = billItems.isNotEmpty() && !hasUnprintedIncrements && !hasPendingDecrements && isCustomerNameValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent, disabledContainerColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth().height(38.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Complete Takeaway Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BatchVoidReasonDialog(
    decreasedItems: List<BillItemUiState>, onConfirm: (reason: String) -> Unit, onDismiss: () -> Unit
) {
    var reasonText by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(6.dp))
            Text("Confirm Item Reductions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("The following printed items were reduced or deleted:", fontSize = 12.sp)
            LazyColumn(modifier = Modifier.heightIn(max = 120.dp)) {
                items(decreasedItems) { item ->
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Text(
                            "• ${item.itemName}: -${item.pendingDecrement} Qty",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(6.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            OutlinedTextField(
                value = reasonText,
                onValueChange = { reasonText = it },
                label = { Text("Reason for Cancellation", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }, confirmButton = {
        Button(
            onClick = { onConfirm(reasonText) },
            enabled = reasonText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Save Void Log & Apply", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Cancel", fontSize = 11.sp) }
    })
}

@Composable
fun BillItemRow(
    item: BillItemUiState, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = when {
                item.pendingDecrement > 0 -> Color(0xFFFEF2F2)
                item.unprintedIncrement > 0 -> GoldAccentLight
                else -> Color(0xFFF8FAFC)
            }
        ), border = BorderStroke(
            1.dp, when {
                item.pendingDecrement > 0 -> Color(0xFFFCA5A5)
                item.unprintedIncrement > 0 -> Color(0xFFFDE68A)
                else -> CardBorderColor
            }
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT ITEM DETAILS
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(4.dp))

                    if (item.pendingDecrement > 0) {
                        Surface(color = Color(0xFFDC2626), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "-${item.pendingDecrement}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    } else if (item.unprintedIncrement > 0) {
                        Surface(color = GoldAccent, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "+${item.unprintedIncrement}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    } else {
                        Surface(color = Color(0xFF94A3B8), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "PRINTED",
                                color = Color.White,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text("Rs. ${item.price.toInt()} x ${item.workingQuantity}", fontSize = 10.sp, color = TextSecondary)
            }

            Spacer(Modifier.width(6.dp))

            // RIGHT COMPACT ACTION CONTROLS (+ - DEL & QUANTITY NUMBER)
            Row(
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // DECREASE BUTTON
                IconButton(
                    onClick = onDecrease, modifier = Modifier.size(28.dp) // Outer button size 28.dp rakha he
                        .background(Color.White, CircleShape).border(1.dp, CardBorderColor, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(14.dp),
                        tint = TextPrimary
                    ) // Inner icon size 14.dp
                }

                Text(
                    text = "${item.workingQuantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // INCREASE BUTTON
                IconButton(
                    onClick = onIncrease, modifier = Modifier.size(28.dp) // Outer button size 28.dp rakha he
                        .background(Color.White, CircleShape).border(1.dp, CardBorderColor, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(14.dp),
                        tint = TextPrimary
                    ) // Inner icon size 14.dp
                }

                Spacer(Modifier.width(2.dp))

                // DELETE BUTTON
                IconButton(
                    onClick = onDelete, modifier = Modifier.size(28.dp) // Outer button size 28.dp rakha he
                        .background(Color(0xFFFEF2F2), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(14.dp)
                    ) // Inner icon size 14.dp
                }
            }
        }
    }
}

fun mapToOrderDetails(
    billItems: List<BillItemUiState>, customerName: String, customerNumber: String, grandTotal: Double
): OrderDetails {
    return OrderDetails(
        userName = customerName,
        phoneNumber = customerNumber,
        address = "N/A",
        foodName = billItems.map { it.itemName },
        foodQuantity = billItems.map { it.workingQuantity },
        foodPrice = billItems.map { it.price.toString() },
        totalPrice = grandTotal.toString()
    )
}