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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PendingOrderScreen(navController: NavHostController, sharedOrderItemViewModel: SharedOrderItemViewModel) {
    val viewModel: PendingViewModel = koinViewModel()
    val orderState by viewModel.state.collectAsState()


    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

            // Header
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),

                contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })

            Text(
                text = "Pending Orders",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 35.sp, color = Color.Black
                )
            )

            when (val state = orderState) {

                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TextColor)
                    }
                }

                is Resource.Success -> {
                    val list = state.data

                    if (list.isEmpty()) {
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
                            items(list) { order ->
                                PendingOrderItemCard(order = order, onClick = {
                                    sharedOrderItemViewModel.setOrderItem(order)
                                    navController.navigate(NavRoutes.OrderDetailsScreen)
                                }, onDispatch = {
                                    // Logic for Dispatch
                                    viewModel.orderDispatch(order)

                                    //want to print recipe here
                                    ReceiptPrinter.printReceipt(order)
                                }, onAccept = {
                                    // Logic for Accept
                                    viewModel.orderAccepted(order)
                                }, onCancel = {
                                    // Logic for Cancel
                                    viewModel.cancelOrder(order)
                                }, onCancelConfirm = {
                                    // Delete Logic
                                    viewModel.cancelConfirm(order)
                                })
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
fun PendingOrderItemCard(
    order: OrderDetails,
    onClick: () -> Unit,
    onDispatch: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onCancelConfirm: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(70.dp), shape = RoundedCornerShape(15.dp), color = Color(0xFFF0F0F0)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo), // Use orderFoodImage
                    contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.userName.toString(),
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Total Price", fontSize = 12.sp, color = Color.Black
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
                modifier = Modifier.width(120.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (order.orderCancel == true) {
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
                        onClick = if (order.orderAccepted == true) onDispatch else onAccept,
                        modifier = Modifier.fillMaxWidth().height(35.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(
                            text = if (order.orderAccepted == true) "Dispatch" else "Accept",
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
