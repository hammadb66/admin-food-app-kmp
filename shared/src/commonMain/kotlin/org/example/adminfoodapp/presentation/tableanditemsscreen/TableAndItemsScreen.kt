package org.example.adminfoodapp.presentation.tableanditemsscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import adminfoodapp.shared.generated.resources.*
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.TablesEntity
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Theme Colors
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryBlueBg = Color(0xFFEFF6FF)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val DangerRed = Color(0xFFEF4444)
private val DangerRedBg = Color(0xFFFEF2F2)
private val SuccessGreen = Color(0xFF10B981)

@Composable
fun TableAndItemsScreen(navController: NavHostController) {
    val viewModel: TableAndItemsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New", modifier = Modifier.size(24.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Navigation Bar
            Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())

            // SEARCH BAR
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search tables or items...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CUSTOM SEGMENTED TAB CONTROLLER
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE2E8F0),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tables Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTab(true) },
                        color = if (uiState.isTableTab) Color.White else Color.Transparent,
                        shadowElevation = if (uiState.isTableTab) 2.dp else 0.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.TableBar,
                                contentDescription = null,
                                tint = if (uiState.isTableTab) PrimaryBlue else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tables",
                                fontSize = 13.sp,
                                fontWeight = if (uiState.isTableTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (uiState.isTableTab) PrimaryBlue else TextMuted
                            )
                        }
                    }

                    // Items Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.setTab(false) },
                        color = if (!uiState.isTableTab) Color.White else Color.Transparent,
                        shadowElevation = if (!uiState.isTableTab) 2.dp else 0.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Fastfood,
                                contentDescription = null,
                                tint = if (!uiState.isTableTab) PrimaryBlue else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "General Items",
                                fontSize = 13.sp,
                                fontWeight = if (!uiState.isTableTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (!uiState.isTableTab) PrimaryBlue else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LIST DISPLAY
            val filteredList = if (uiState.isTableTab) {
                uiState.tables.filter { it.tableNumber.contains(uiState.searchQuery, ignoreCase = true) }
            } else {
                uiState.items.filter { it.itemName.contains(uiState.searchQuery, ignoreCase = true) }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isTableTab) "No tables found" else "No items found",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = {
                        if (it is TablesEntity) "table_${it.id}" else "item_${(it as GenralItemsEntity).id}"
                    }) { item ->
                        if (item is TablesEntity) {
                            ItemCard(
                                title = item.tableNumber,
                                sub = "${item.capacity} Persons Capacity",
                                isTable = true,
                                onEdit = { viewModel.startEditing(item) },
                                onDelete = { viewModel.requestDelete(item) }
                            )
                        } else if (item is GenralItemsEntity) {
                            ItemCard(
                                title = item.itemName,
                                sub = "Rs. ${item.price}",
                                isTable = false,
                                onEdit = { viewModel.startEditing(item) },
                                onDelete = { viewModel.requestDelete(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.requestDelete(null) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text("Delete Confirmation", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            },
            text = {
                Text("Are you sure you want to delete this record? This action cannot be undone.", fontSize = 13.sp, color = TextMuted)
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.requestDelete(null) }) {
                    Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Add Dialog Trigger
    if (showAddDialog) {
        if (uiState.isTableTab) {
            AddTableDialog(onDismiss = { showAddDialog = false }, onConfirm = { n, c -> viewModel.addTable(n, c) })
        } else {
            AddItemDialog(onDismiss = { showAddDialog = false }, onConfirm = { n, p -> viewModel.addItem(n, p) })
        }
    }

    // Edit Dialog Trigger
    if (uiState.itemToEdit != null) {
        val entity = uiState.itemToEdit
        if (entity is TablesEntity) {
            AddTableDialog(
                existing = entity,
                onDismiss = { viewModel.startEditing(null) },
                onConfirm = { n, c -> viewModel.updateTable(entity.id, n, c) }
            )
        } else if (entity is GenralItemsEntity) {
            AddItemDialog(
                existing = entity,
                onDismiss = { viewModel.startEditing(null) },
                onConfirm = { n, p -> viewModel.updateItem(entity.id, n, p) }
            )
        }
    }
}

@Composable
fun ItemCard(
    title: String,
    sub: String,
    isTable: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isTable) PrimaryBlueBg else Color(0xFFECFDF5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isTable) Icons.Default.TableBar else Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = if (isTable) PrimaryBlue else SuccessGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isTable) Icons.Default.Groups else Icons.Default.Payments,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sub,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onEdit() },
                    color = PrimaryBlueBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDelete() },
                    color = DangerRedBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTableDialog(
    existing: TablesEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var name by remember { mutableStateOf(existing?.tableNumber ?: "") }
    var cap by remember { mutableStateOf(existing?.capacity?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = if (existing == null) "Add New Table" else "Edit Table Info",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Table Name / Number") },
                    placeholder = { Text("e.g. Table 05") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                OutlinedTextField(
                    value = cap,
                    onValueChange = { cap = it },
                    label = { Text("Seating Capacity") },
                    placeholder = { Text("e.g. 4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), cap.toLongOrNull() ?: 0)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (existing == null) "Save Table" else "Update Table", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun AddItemDialog(
    existing: GenralItemsEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf(existing?.itemName ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = if (existing == null) "Add Quick Item" else "Edit Quick Item",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g. Mineral Water") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (Rs)") },
                    placeholder = { Text("e.g. 80") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), price.toDoubleOrNull() ?: 0.0)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (existing == null) "Save Item" else "Update Item", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}