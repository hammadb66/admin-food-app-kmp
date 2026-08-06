package org.example.adminfoodapp.presentation.dineinscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Restaurant
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
import org.example.adminfoodapp.utils.TABLESTATUS
import org.koin.compose.viewmodel.koinViewModel

// Color Palette Matching Main POS Screen
private val SurfaceBackground = Color(0xFFF8FAFC)
private val PrimaryAccent = Color(0xFF059669)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)
private val StatusAvailableBg = Color(0xFFECFDF5)
private val StatusAvailableText = Color(0xFF059669)
private val StatusOccupiedBg = Color(0xFFFEF2F2)
private val StatusOccupiedText = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(navController: NavHostController) {
    val viewModel: DineInViewModel = koinViewModel()
    val tablesList by viewModel.tablesList.collectAsState()

    val availableCount = tablesList.count { it.status == TABLESTATUS.AVAILABLE.id }
    val occupiedCount = tablesList.size - availableCount
        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(20.dp)
        ) {
            // Stats Header Pills Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TableStatSummaryChip(
                    label = "Total",
                    count = tablesList.size,
                    color = TextPrimary,
                    bgColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                TableStatSummaryChip(
                    label = "Available",
                    count = availableCount,
                    color = StatusAvailableText,
                    bgColor = StatusAvailableBg,
                    modifier = Modifier.weight(1f)
                )
                TableStatSummaryChip(
                    label = "Occupied",
                    count = occupiedCount,
                    color = StatusOccupiedText,
                    bgColor = StatusOccupiedBg,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Grid or Empty State
            if (tablesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.size(70.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Koi Table mojood nahi hai",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bottom right button se naya table add karein!",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 170.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tablesList) { table ->
                        TableCardItem(
                            table = table,
                            onStatusToggle = {
                                navController.navigate(
                                    NavRoutes.OrderBillingScreen(table.id, table.tableNumber)
                                )
                            }
                        )
                    }
                }
            }
        }

}

@Composable
fun TableStatSummaryChip(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Text(text = count.toString(), fontSize = 14.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TableCardItem(
    table: TablesEntity,
    onStatusToggle: () -> Unit,
) {
    val isAvailable = table.status == TABLESTATUS.AVAILABLE.id
    val badgeBg = if (isAvailable) StatusAvailableBg else StatusOccupiedBg
    val statusColor = if (isAvailable) StatusAvailableText else StatusOccupiedText
    val statusText = if (isAvailable) "Available" else "Occupied"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onStatusToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Table Number & Status Dot Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = table.tableNumber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Seats Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EventSeat,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${table.capacity} Capacity Seats",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Action Tag
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isAvailable) PrimaryAccent.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAvailable) "Tap to Open Bill" else "Tap to Manage Order",
                        color = if (isAvailable) PrimaryAccent else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}