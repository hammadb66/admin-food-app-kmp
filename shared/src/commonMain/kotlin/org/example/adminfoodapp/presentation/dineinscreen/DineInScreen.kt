package org.example.adminfoodapp.presentation.dineinscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.TablesEntity
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TableManagementScreen(navController: NavHostController) {
    val viewModel: DineInViewModel = koinViewModel()
    val tablesList by viewModel.tablesList.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Dine-In Tables", fontWeight = FontWeight.Bold) }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            if (tablesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Koi Table mojood nahi hai.\nBottom right button se naya table add karein!",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 10.dp)

                ) {
                    items(tablesList) { table ->
                        // TableCardItem Click Handler inside TableManagementScreen:
                        TableCardItem(
                            table = table, onStatusToggle = {
                                navController.navigate(NavRoutes.OrderBillingScreen(table.id, table.tableNumber))
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun TableCardItem(
    table: TablesEntity,
    onStatusToggle: () -> Unit,
) {
    val isAvailable = table.status == "AVAILABLE"
    val cardBgColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val statusTextColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp))
            .clickable { onStatusToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = table.tableNumber, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${table.capacity} Seats", fontSize = 14.sp, color = Color.DarkGray
            )

            Box(
                modifier = Modifier.background(statusTextColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isAvailable) "AVAILABLE (Click to Book)" else "OCCUPIED (Click to Free)",
                    color = statusTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}