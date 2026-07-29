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
import org.example.adminfoodapp.database.model.OrderDetails
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
            when (val state = orderState) {

                is Resource.Loading -> {}
                is Resource.Success -> {
                    val list = state.data


                    if (list.isEmpty()) {
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
                            items(list) { order ->
                                DeliveryItemCard(order)
                            }
                        }
                    }

                }

                is Resource.Error -> {}

            }
        }
    }
}

@Composable
fun DeliveryItemCard(order: OrderDetails) {
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
                    text = order.userName.toString(),
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Payment", fontSize = 14.sp, color = Color(0xFFB3B3B3)
                )
                Text(
                    text = order.totalPrice.toString(),
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TextColor
                )
            }

            // Right Side: Status Text & Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (order.cancelConfirm == false) {
                    TextButton(modifier = Modifier.background(shape =RoundedCornerShape(20.dp), color = TextColor),
                        onClick = {
                            ReceiptPrinter.printReceipt(order)
                        },
                    ) {
                        Text(
                            text = "Print Bill",
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = if (order.cancelConfirm == true) "Cancelled" else "Delivered",
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(if (order.cancelConfirm == true) Color.Red else TextColor)
                )
            }
        }
    }
}
