package org.example.adminfoodapp.presentation.pendingorderscreen

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
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
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private val BackgroundColor = Color(0xFFF8FAFC)
private val GreenAccept = Color(0xFF10B981)
private val GreenAcceptBg = Color(0xFFECFDF5)
private val BlueDispatch = Color(0xFF2563EB)
private val BlueDispatchBg = Color(0xFFEFF6FF)
private val RedCancel = Color(0xFFEF4444)
private val RedCancelBg = Color(0xFFFEF2F2)
private val AmberWarning = Color(0xFFD97706)
private val AmberWarningBg = Color(0xFFFFFBEB)
private val CardBorder = Color(0xFFE2E8F0)
private val BlueReport = Color(0xFF1E40AF)
private val BlueReportBg = Color(0xFFEFF6FF)

@Composable
fun PendingOrderScreen(
    navController: NavHostController,
    sharedOrderItemViewModel: SharedOrderItemViewModel
) {
    val viewModel: PendingViewModel = koinViewModel()
    val orderState by viewModel.state.collectAsState()

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

            // Top Header Bar
            Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Dynamic Count Badge
                val pendingCount = (orderState as? Resource.Success)?.data?.size ?: 0
                Surface(
                    color = BlueReportBg,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 10.dp
                ) {
                    Text(
                        text = "$pendingCount New",
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

                    if (list.isEmpty()) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No New Online Orders Found",
                                color = TextColor,
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(
                                items = list,
                                key = { order -> order.itemPushKey ?: order.hashCode() }
                            ) { order ->
                                Box(modifier = Modifier.animateItem()) {
                                    PendingOrderItemCard(
                                        order = order,
                                        onClick = {
                                            sharedOrderItemViewModel.setOrderItem(order)
                                            navController.navigate(NavRoutes.OrderDetailsScreen)
                                        },
                                        onDispatch = {
                                            viewModel.orderDispatch(order)
                                            ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                                        },
                                        onAccept = {
                                            viewModel.orderAccepted(order)
                                            ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                                        },
                                        onCancel = {
                                            viewModel.cancelOrder(order)
                                        },
                                        onCancelConfirm = {
                                            viewModel.cancelConfirm(order)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading orders", color = RedCancel)
                    }
                }
            }
        }
    }
}

@Composable
fun PendingOrderItemCard(
    order: OrderDetails,
    onClick: () -> Unit,
    onDispatch: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onCancelConfirm: () -> Unit
) {
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
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: Type Badge + Dynamic Time Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BlueDispatchBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ONLINE ORDER",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueDispatch
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Dynamic Order Urgency Time Badge
                    OrderTimeBadge(orderTimeMillis = order.currentTime ?: System.currentTimeMillis())
                }

                val (statusText, statusColor) = when {
                    order.orderCancel == true -> "Cancelled" to RedCancel
                    order.orderAccepted == true -> "Accepted" to GreenAccept
                    else -> "Pending" to AmberWarning
                }

                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Info: Image + Details
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(65.dp),
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
                        fontSize = 20.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Total Amount",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Rs ${order.totalPrice}",
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 20.sp,
                        color = TextColor
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = CardBorder
            )

            // Bottom Action Buttons with Icons
            if (order.orderCancel == true) {
                Button(
                    onClick = onCancelConfirm,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedCancel)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Delete", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isAccepted = order.orderAccepted == true

                    // Accept / Dispatch Button
                    Button(
                        onClick = if (isAccepted) onDispatch else onAccept,
                        modifier = Modifier.weight(1.2f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAccepted) BlueDispatch else GreenAccept
                        )
                    ) {
                        Icon(
                            imageVector = if (isAccepted) Icons.Default.LocalShipping else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAccepted) "Dispatch" else "Accept",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Cancel Button
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(0.9f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, RedCancel),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedCancel)
                    ) {
                        Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Print Thermal Receipt Button
                    Button(
                        onClick = { ReceiptPrinter.printReceipt(order, PrintMode.THERMAL) },
                        modifier = Modifier.weight(0.9f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Icon(
                            Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTimeBadge(orderTimeMillis: Long) {
    val currentTime = System.currentTimeMillis()
    val diffMinutes = ((currentTime - orderTimeMillis) / (1000 * 60)).coerceAtLeast(0)

    val (badgeBg, textColor) = when {
        diffMinutes >= 20 -> RedCancelBg to RedCancel       // Highly Urgent (> 20 mins)
        diffMinutes >= 10 -> AmberWarningBg to AmberWarning  // Warning (> 10 mins)
        else -> GreenAcceptBg to GreenAccept                 // Fresh Order
    }

    Surface(
        color = badgeBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = if (diffMinutes == 0L) "Just now" else "$diffMinutes m ago",
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}