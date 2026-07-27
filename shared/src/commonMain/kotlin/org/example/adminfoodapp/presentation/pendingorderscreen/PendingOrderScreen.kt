package org.example.adminfoodapp.presentation.pendingorderscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

// Data Model matching OrderDetails
data class PendingOrder(
    val itemPushKey: String,
    val userName: String,
    val totalPrice: String,
    var orderAccepted: Boolean = false,
    var orderCancel: Boolean = false,
    val foodImages: List<String> = emptyList()
)

@Composable
fun PendingOrderScreen(

) {
    // Ye data ViewModel se aana chahiye
    var orders by remember {
        mutableStateOf(
            listOf(
                PendingOrder("1", "Hammad Baloch", "1500", orderAccepted = false),
                PendingOrder("2", "John Doe", "800", orderAccepted = true),
                PendingOrder("3", "Jane Smith", "1200", orderCancel = true)
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            
            // Header
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),

                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {  }
            )

            Text(
                text = "Pending Orders",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 35.sp,
                    color = Color.Black
                )
            )

            // Order List or Empty State
            if (orders.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "No New Order Found",
                        color = TextColor,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orders) { order ->
                        PendingOrderItemCard(
                            order = order,
                            onClick = {  },
                            onAccept = { 
                                // Logic for Accept/Dispatch
                                orders = orders.map { 
                                    if(it.itemPushKey == order.itemPushKey) it.copy(orderAccepted = true) else it 
                                }
                            },
                            onCancel = {
                                // Logic for Cancel
                                orders = orders.map { 
                                    if(it.itemPushKey == order.itemPushKey) it.copy(orderCancel = true) else it 
                                }
                            },
                            onCancelConfirm = {
                                // Delete Logic
                                orders = orders.filter { it.itemPushKey != order.itemPushKey }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingOrderItemCard(
    order: PendingOrder,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onCancelConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFFF0F0F0)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo), // Use orderFoodImage
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.userName,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Total Price",
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = "Rs ${order.totalPrice}",
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = TextColor
                )
            }

            // Actions
            Column(
                modifier = Modifier.width(120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (order.orderCancel) {
                    // Show Cancel Confirm if order is cancelled
                    Button(
                        onClick = onCancelConfirm,
                        modifier = Modifier.fillMaxWidth().height(35.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text("Cancel Confirm", fontSize = 12.sp, color = Color.White)
                    }
                } else {
                    // Accept/Dispatch Button
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth().height(35.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(
                            text = if (order.orderAccepted) "Dispatch" else "Accept",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    // Cancel Order Button
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth().height(35.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text("Order Cancel", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
