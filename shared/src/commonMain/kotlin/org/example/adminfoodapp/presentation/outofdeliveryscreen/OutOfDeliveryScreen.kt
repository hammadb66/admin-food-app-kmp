package org.example.adminfoodapp.presentation.outofdeliveryscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// POS Theme Colors
private val BackgroundColor = Color(0xFFF8FAFC)
private val GreenDelivered = Color(0xFF10B981)
private val GreenDeliveredBg = Color(0xFFECFDF5)
private val BlueReport = Color(0xFF1E40AF)
private val BlueReportBg = Color(0xFFEFF6FF)
private val RedCancelled = Color(0xFFEF4444)
private val RedCancelledBg = Color(0xFFFEF2F2)
private val CardBorder = Color(0xFFE2E8F0)

@Composable
fun OutOfDeliveryScreen(navController: NavHostController) {

    val viewmodel: OutOfDeliveryViewModel = koinViewModel()
    val orderState by viewmodel.state.collectAsState()

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
            // Header Bar
            Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Deliveries Count Badge
                val deliveryCount = (orderState as? Resource.Success)?.data?.size ?: 0
                Surface(
                    color = BlueReportBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$deliveryCount Orders",
                        color = BlueReport,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (val state = orderState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TextColor)
                    }
                }

                is Resource.Success -> {
                    val list = state.data

                    if (list.isNotEmpty()) {
                        // Batch A4 Summary Print Header Button
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
                                        text = "Print all current active delivery receipts",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Button(
                                    onClick = { ReceiptPrinter.printAllOrders(list) },
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

                        // Orders List
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = list,
                                key = { order -> order.itemPushKey ?: order.hashCode() }
                            ) { order ->
                                Box(modifier = Modifier.animateItem()) {
                                    DeliveryItemCard(order = order)
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Orders Out For Delivery",
                                    color = TextColor,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading delivery history", color = RedCancelled)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryItemCard(order: OrderDetails) {
    val isCancelled = order.cancelConfirm == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Card Header: Delivery Tag + Dynamic Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DISPATCHED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                // Dynamic Status Chip
                Surface(
                    color = if (isCancelled) RedCancelledBg else GreenDeliveredBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCancelled) Icons.Default.Cancel else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (isCancelled) RedCancelled else GreenDelivered
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCancelled) "Cancelled" else "Deliver",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCancelled) RedCancelled else GreenDelivered
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info Row: Customer Name & Price
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.userName ?: "Guest Customer",
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 18.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total Amount: Rs ${order.totalPrice}",
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 16.sp,
                        color = TextColor
                    )
                }

                // Single Thermal Receipt Printing Button
                Button(
                    onClick = { ReceiptPrinter.printReceipt(order, PrintMode.THERMAL) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCancelled) RedCancelled else Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isCancelled) "Void Slip" else "Print Bill",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}