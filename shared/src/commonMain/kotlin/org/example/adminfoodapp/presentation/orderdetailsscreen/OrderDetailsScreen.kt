package org.example.adminfoodapp.presentation.orderdetailsscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print

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
import coil3.compose.AsyncImage
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel
import org.example.adminfoodapp.utils.PrintMode
import org.example.adminfoodapp.utils.ReceiptPrinter
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource


@Composable
fun OrderDetailsScreen(navController: NavHostController, sharedOrderItemViewModel: SharedOrderItemViewModel) {
    val orderItem by sharedOrderItemViewModel.orderItem.collectAsState()



    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp).statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),

                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp).clickable { navController.navigateUp() })

                Text(
                    text = "Order Details",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 34.sp, color = Color.Black
                    )
                )
                // Yahan Print ka button laga diya!
                IconButton(
                    onClick = {
                        orderItem?.let { order ->
                            ReceiptPrinter.printReceipt(order, PrintMode.THERMAL)
                        }
                    }
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Print Order", tint = Color.Black)
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Customer Info Fields
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderDetailInfoField(label = "Name", value = orderItem?.userName ?: "")
                        OrderDetailInfoField(label = "Address", value = orderItem?.address ?: "")
                        OrderDetailInfoField(label = "Phone", value = orderItem?.phoneNumber ?: "")
                        OrderDetailInfoField(label = "Total Price", value = orderItem?.totalPrice ?: "")

                        // Optional: Cancel By (Hidden by default in XML)
                        // OrderDetailInfoField(label = "Cancel By", value = "Admin")

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                val foodNames = orderItem?.foodName ?: emptyList()
                val foodPrices = orderItem?.foodPrice ?: emptyList()
                val foodQuantities = orderItem?.foodQuantity ?: emptyList()
                val foodImages = orderItem?.foodImages ?: emptyList()

                items(foodNames.size) { index ->
                    OrderedItemCard(
                        name = foodNames.getOrNull(index)?.toString() ?: "Unknown",
                        price = foodPrices.getOrNull(index)?.toString() ?: "0",
                        quantity = foodQuantities.getOrNull(index)?.toString() ?: "1",
                        image = foodImages.getOrNull(index)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDetailInfoField(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        //.height(56.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.width(100.dp),
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = value,
                modifier = Modifier.weight(1f),
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun OrderedItemCard(name: String, price: String, quantity: String, image: Any?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(70.dp), shape = RoundedCornerShape(15.dp), color = Color(0xFFF0F0F0)
            ) {
                AsyncImage(
                    model = image ?: Res.drawable.logo, // Use menu1
                    contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Quantity: ",
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = quantity,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                        fontSize = 16.sp,
                        color = TextColor
                    )
                }
            }

            Text(
                text = price,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                fontSize = 20.sp,
                color = TextColor,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
