package org.example.adminfoodapp.presentation.addgeneralitem

import GoldAccent
import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.arrow_left
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.example.adminfoodapp.presentation.edititemscreen.AddItemTextField
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddGeneralItemScreen(navController: NavController) {
    var foodName by remember { mutableStateOf("") }
    var foodPrice: String by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val viewModel: AddGeneralItemViewModel = koinViewModel()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7F7F9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(220.dp).background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B1B1B), Color(0xFF3A3A3A))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).statusBarsPadding().fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            painterResource(Res.drawable.arrow_left), contentDescription = "Back", tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "New Menu Item",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                        )
                        Text(
                            text = "Add a general item to your menu",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().offset(y = (-28).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier.width(48.dp).height(4.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(38.dp))

                    errorMessage?.let { msg ->
                        Surface(
                            color = Color(0xFFFDECEC),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Text(
                        text = "ITEM NAME",
                        color = TextColor.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    AddItemTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = "e.g. Chicken Biryani",
                        accentColor = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PRICE",
                        color = TextColor.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    AddItemTextField(
                        value = foodPrice,
                        onValueChange = { foodPrice = it },
                        label = "0.00",
                        accentColor = GoldAccent,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (foodName.isBlank() || foodPrice.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }
                            val priceValue = foodPrice.trim().toDoubleOrNull()
                            if (priceValue == null) {
                                errorMessage = "Please enter a valid price"
                                return@Button
                            }

                            errorMessage = null
                            isSaving = true
                            viewModel.addNewGeneralItem(foodName.trim(), priceValue, "General Item")
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            disabledContainerColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Add Item",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}