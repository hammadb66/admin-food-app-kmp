package org.example.adminfoodapp.presentation.edituserscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditUserScreen(navController: NavHostController, shareUserItemViewModel: ShareUserItemViewModel) {
    val shareViewModel by shareUserItemViewModel.userModel.collectAsState()
    val viewmodel: EditUserViewModel = koinViewModel()
    var name by remember { mutableStateOf(shareViewModel?.name ?: "") }
    var password by remember { mutableStateOf(shareViewModel?.password ?: "") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var userAccess by remember { mutableStateOf(shareViewModel?.userAccess ?: false) }
    var adminAccess by remember { mutableStateOf(shareViewModel?.admin ?: false) }
    val scrollState = rememberScrollState()



    // Screen Background
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), // Light subtle gray background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Main Card Wrap (Acts as a clean container on Desktop/Mobile)
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp) // Limits width on Desktop
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Header: Back Button
                    Row(
                        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(CircleShape).clickable { navController.navigateUp() },
                            color = Color(0xFFF1F3F5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_left),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    // Logo & App Name
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Waves Of Food", style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                            fontSize = 32.sp,
                            color = Color.Black
                        )
                    )

                    Text(
                        text = "Update User", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Input: User Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("User Name") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.usernew),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextColor,
                            focusedLabelColor = TextColor,
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input: Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.lock),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = painterResource(Res.drawable.eye),
                                    contentDescription = "Toggle Password",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextColor,
                            focusedLabelColor = TextColor,

                            )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Access Switches Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccessToggleCard(
                            title = "User Access",
                            isChecked = userAccess,
                            onCheckedChange = { userAccess = it },
                            modifier = Modifier.weight(1f)
                        )

                        AccessToggleCard(
                            title = "Admin Access",
                            isChecked = adminAccess,
                            onCheckedChange = { adminAccess = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Update Button (Gradient Accent)
                    Button(
                        onClick = {
                            val updatedUser = UserModel(
                                key = shareViewModel?.key,
                                name = name,
                                password = password,
                                userAccess = userAccess,
                                admin = adminAccess

                            )

                            println("$updatedUser  ,  $shareViewModel")

                            if (updatedUser != shareViewModel) {
                                viewmodel.updateUser(updatedUser) {
                                    navController.navigateUp()
                                }

                            } else {
                                navController.navigateUp()

                            }

                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Update User",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                        )
                    }
                }
            }

            // Footer Design Credit
            Text(
                text = "Design By\nHammad Baloch",
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
            )
        }
    }
}

@Composable
fun AccessToggleCard(
    title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isChecked) Color(0xFF2E7D32) else Color(0xFFE0E0E0), label = "BorderColor"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isChecked) Color(0xFFE8F5E9) else Color(0xFFFAFAFA), label = "BgColor"
    )

    Surface(
        modifier = modifier.border(1.5.dp, animatedBorderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)).clickable { onCheckedChange(!isChecked) }, color = animatedBgColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isChecked) Color(0xFF2E7D32) else Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Switch(
                checked = isChecked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2E7D32),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD6D6D6)
                )
            )
        }
    }
}