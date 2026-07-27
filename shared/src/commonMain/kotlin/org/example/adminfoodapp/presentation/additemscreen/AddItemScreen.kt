package org.example.adminfoodapp.presentation.additemscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddItemScreen() {
    var foodName by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val goldAccent = Color(0xFFD4AF37) // Gold Accent from XML
    val darkGray = Color(0xFF333333)

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp)
            ) {
                // Background Image Placeholder
                Image(
                    painter = painterResource(Res.drawable.image_placeholder), // Use image_placeholder
                    contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                )

                // Top Gradient Scrim
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
                )

                // Back Button & Title
                Row(
                    modifier = Modifier.padding(16.dp).statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(45.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(painterResource(Res.drawable.arrow_left), contentDescription = "Back", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "New Menu Item",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )
                }

                // Floating Change Image Button
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp, end = 20.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { /* Select Image */ },
                        containerColor = goldAccent,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(55.dp)
                    ) {
                        Icon(painterResource(Res.drawable.pluse_round), contentDescription = "Add Image")
                    }
                }
            }

            // Form Sheet Section
            Surface(
                modifier = Modifier.fillMaxWidth().offset(y = (-30).dp), // Overlap effect
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White, tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier.width(50.dp).height(5.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Form Fields
                    AddItemTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = "Item Name",
                        accentColor = goldAccent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddItemTextField(
                        value = foodPrice,
                        onValueChange = { foodPrice = it },
                        label = "Price",
                        accentColor = goldAccent,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Description",
                        color = darkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("A short, elegant description") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = goldAccent, focusedLabelColor = goldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Ingredients",
                        color = darkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { ingredients = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Noodle, Salad, Burger...") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = goldAccent, focusedLabelColor = goldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Add Item Button
                    Button(
                        onClick = { /* Add Item Action */ },
                        modifier = Modifier.fillMaxWidth().height(65.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text(
                            text = "Add Item",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    accentColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor, focusedLabelColor = accentColor
        )
    )
}
