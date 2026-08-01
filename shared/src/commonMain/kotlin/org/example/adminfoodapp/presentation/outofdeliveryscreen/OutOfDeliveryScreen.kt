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
import androidx.compose.material.icons.filled.Print

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
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OutOfDeliveryScreen(navController: NavHostController) {

    val viewmodel: OutOfDeliveryViewModel = koinViewModel()
    val orderState by viewmodel.state.collectAsState()
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),

                contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })

            Text(
                text = "Out For Delivery",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 35.sp, color = Color.Black
                )
            )


            // ... baki code ...
            when (val state = orderState) {
                is Resource.Loading -> { /* Loading UI */ }

                is Resource.Success -> {
                    val list = state.data // Yahan list milti hai

                    // BUTTON KO YAHAN RAKHO taake usay 'list' mil sake
                    if (list.isNotEmpty()) {
                        Button(
                            onClick = {
                                // Ab loop nahi, direct function call
                                ReceiptPrinter.printAllOrders(list)
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Icon(androidx.compose.material.icons.Icons.Default.Print, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Print All Pending Deliveries (A4 Report)")
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(list) { order ->
                                DeliveryItemCard(order)
                            }
                        }
                    } else {
                        // Empty state handle karo
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No Orders Out For Delivery", color = TextColor, fontSize = 18.sp)
                        }
                    }
                }
                is Resource.Error -> { /* Error handling */ }
            }
        }
    }
}

@Composable
fun DeliveryItemCard(order: OrderDetails) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = order.userName.toString(), fontSize = 16.sp, color = Color.Black)
                Text(text = "Price: ${order.totalPrice}", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = TextColor)
            }

            // Buttons Column
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Printing Button logic
                Button(
                    onClick = {
                        // Agar cancel hai toh Void slip, warna Bill print karo
                        ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (order.cancelConfirm == true) Color.Red else TextColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Print, null, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(if (order.cancelConfirm == true) "Print Void" else "Print Bill")
                }

                Text(
                    text = if (order.cancelConfirm == true) "Cancelled" else "Delivered",
                    fontSize = 14.sp, color = Color.Gray
                )
            }
        }
    }
}
