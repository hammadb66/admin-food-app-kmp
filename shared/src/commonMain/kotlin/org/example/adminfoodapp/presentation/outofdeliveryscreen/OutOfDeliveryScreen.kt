package org.example.adminfoodapp.presentation.outofdeliveryscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

// Data Model matching your OrderDetails
data class OrderDelivery(
    val userName: String,
    val totalPrice: String,
    val isCancelled: Boolean,
    val itemPushKey: String
)

@Composable
fun OutOfDeliveryScreen(navController: NavHostController) {
    // Dummy Data
    val orders = listOf(
        OrderDelivery("John Doe", "$30", false, "k1"),
        OrderDelivery("Jane Smith", "$45", true, "k2"),
        OrderDelivery("Alex Brown", "$20", false, "k3")
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),

                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigateUp() }
            )

            Text(
                text = "Out For Delivery",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 35.sp,
                    color = Color.Black
                )
            )

            // List or Empty State
            if (orders.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "No Orders Out For Delivery",
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
                        DeliveryItemCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryItemCard(order: OrderDelivery) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Name, Payment, Price
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.userName,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Payment",
                    fontSize = 14.sp,
                    color = Color(0xFFB3B3B3)
                )
                Text(
                    text = order.totalPrice,
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TextColor
                )
            }

            // Right Side: Status Text & Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (order.isCancelled) "Cancelled" else "Delivered",
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status Indicator (The colored circle from XML)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (order.isCancelled) Color.Red else TextColor)
                )
            }
        }
    }
}
