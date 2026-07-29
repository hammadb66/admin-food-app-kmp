package org.example.adminfoodapp.presentation.orderbillingscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.koin.compose.viewmodel.koinViewModel

// Quick Menu Selection Catalog (Testing ke liye sample food items)
data class MenuItemSample(val id: Long, val name: String, val price: Double)

val sampleMenuItems = listOf(
    MenuItemSample(1, "Chicken Pizza", 1200.0),
    MenuItemSample(2, "Zinger Burger", 450.0),
    MenuItemSample(3, "Club Sandwich", 350.0),
    MenuItemSample(4, "Fries (Large)", 200.0),
    MenuItemSample(5, "Cold Drink 1.5L", 180.0),
    MenuItemSample(6, "Mineral Water", 80.0)
)

@Composable
fun OrderBillingScreen(args: NavRoutes.OrderBillingScreen, navController: NavHostController) {
    val viewModel: OrderBillingViewModel = koinViewModel()
    val orderItems by viewModel.orderItems.collectAsState()

    val tableId=args.id
    val tableNumber=args.number

    // Screen start hotay hi is table ka order/session attach karein
    LaunchedEffect(tableId) {
        viewModel.loadTableOrder(tableId)
    }

    // Checkout hone par auto back navigation
    LaunchedEffect(Unit) {
        viewModel.checkoutSuccess.collect {
//            onBackClick()
        }
    }

    val totalBill = orderItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text("Table $tableNumber - Billing & Order", fontWeight = FontWeight.Bold)
            }, navigationIcon = {
                IconButton(onClick = {navController.navigateUp()}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            )
        }) { innerPadding ->
        Row(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ================= LEFT SIDE: MENU SELECTION CATALOG =================
            Column(
                modifier = Modifier.weight(1.2f).fillMaxHeight()
            ) {
                Text(
                    "Quick Menu Catalog",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sampleMenuItems) { menu ->
                        Card(
                            onClick = {
                                viewModel.addItemToOrder(menu.id, menu.name, menu.price)
                            }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(menu.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            // ================= RIGHT SIDE: LIVE ORDER SUMMARY & BILL =================
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    Text(
                        "Current Table Receipt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Order Items List
                    if (orderItems.isEmpty()) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Text("No items added yet", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(orderItems, key = { it.id }) { item ->
                                OrderItemRow(
                                    item = item,
                                    onIncrease = { viewModel.updateQuantity(item, 1) },
                                    onDecrease = { viewModel.updateQuantity(item, -1) },
                                    onRemove = { viewModel.removeItem(item) })
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Bill Total Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Rs. ${totalBill.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkout Button
                    Button(
                        onClick = { viewModel.checkoutAndPrintBill() },
                        enabled = orderItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Checkout & Free Table", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(
    item: OrderItemsEntity, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.itemName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "Rs. ${item.price.toInt()} x ${item.quantity} = Rs. ${(item.price * item.quantity).toInt()}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}