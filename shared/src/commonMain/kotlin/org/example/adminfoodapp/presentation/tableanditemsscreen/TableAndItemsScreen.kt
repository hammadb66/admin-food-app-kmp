package org.example.adminfoodapp.presentation.tableanditemsscreen

import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.arrow_left
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.GenralItemsEntity
import org.example.adminfoodapp.database.TablesEntity
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Import add kar lena
import androidx.compose.material.icons.filled.Edit

@Composable
fun TableAndItemsScreen(navController: NavHostController) {
    val viewModel: TableAndItemsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(painter = painterResource(Res.drawable.arrow_left), "")
                }
            },
            title = { Text("Add Tables or General Items") },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(Icons.Default.Add, "Add")
        }
    }) { padding ->
        Column(Modifier.padding(padding)) {

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Search here...") },
                singleLine = true
            )

            TabRow(selectedTabIndex = if (uiState.isTableTab) 0 else 1) {
                Tab(selected = uiState.isTableTab, onClick = { viewModel.setTab(true) }, text = { Text("Tables") })
                Tab(selected = !uiState.isTableTab, onClick = { viewModel.setTab(false) }, text = { Text("Items") })
            }

            val filteredList = if (uiState.isTableTab) {
                uiState.tables.filter { it.tableNumber.contains(uiState.searchQuery, ignoreCase = true) }
            } else {
                uiState.items.filter { it.itemName.contains(uiState.searchQuery, ignoreCase = true) }
            }

            LazyColumn(
                Modifier.padding(8.dp), contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = {
                    if (it is TablesEntity) it.id else (it as GenralItemsEntity).id
                }) { item ->
                    if (item is TablesEntity) {
                        ItemCard(
                            title = item.tableNumber,
                            sub = "Cap: ${item.capacity}",
                            onEdit = { viewModel.startEditing(item) }, // Edit trigger
                            onDelete = { viewModel.requestDelete(item) })
                    } else if (item is GenralItemsEntity) {
                        ItemCard(
                            title = item.itemName,
                            sub = "Rs. ${item.price}",
                            onEdit = { viewModel.startEditing(item) }, // Edit trigger
                            onDelete = { viewModel.requestDelete(item) })
                    }
                }
            }
        }
    }

    // Delete Dialog
    if (uiState.itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.requestDelete(null) },
            title = { Text("Delete Confirmation") },
            text = { Text("Kya aap yakeen se delete karna chahte hain?") },
            confirmButton = { Button(onClick = { viewModel.confirmDelete() }) { Text("Confirm") } })
    }

    // Add/Edit Logic
    if (showAddDialog) {
        if (uiState.isTableTab) {
            AddTableDialog(onDismiss = { showAddDialog = false }, onConfirm = { n, c -> viewModel.addTable(n, c) })
        } else {
            AddItemDialog(onDismiss = { showAddDialog = false }, onConfirm = { n, p -> viewModel.addItem(n, p) })
        }
    }

    if (uiState.itemToEdit != null) {
        val entity = uiState.itemToEdit
        if (entity is TablesEntity) {
            AddTableDialog(
                existing = entity,
                onDismiss = { viewModel.startEditing(null) },
                onConfirm = { n, c -> viewModel.updateTable(entity.id, n, c) })
        } else if (entity is GenralItemsEntity) {
            AddItemDialog(
                existing = entity,
                onDismiss = { viewModel.startEditing(null) },
                onConfirm = { n, p -> viewModel.updateItem(entity.id, n, p) })
        }
    }
}

@Composable
fun ItemCard(title: String, sub: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit", tint = Color.Blue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
        }
    }
}

@Composable
fun AddTableDialog(existing: TablesEntity? = null, onDismiss: () -> Unit, onConfirm: (String, Long) -> Unit) {
    var name by remember { mutableStateOf(existing?.tableNumber ?: "") }
    var cap by remember { mutableStateOf(existing?.capacity?.toString() ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Table" else "Edit Table") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Table Name") })
                OutlinedTextField(value = cap, onValueChange = { cap = it }, label = { Text("Capacity") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, cap.toLongOrNull() ?: 0); onDismiss()
            }) { Text(if (existing == null) "Add" else "Update") }
        })
}

@Composable
fun AddItemDialog(existing: GenralItemsEntity? = null, onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf(existing?.itemName ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Item" else "Edit Item") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, price.toDoubleOrNull() ?: 0.0); onDismiss()
            }) { Text(if (existing == null) "Add" else "Update") }
        })
}