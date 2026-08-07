package org.example.adminfoodapp.presentation.edituserscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Theme Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryBlueBg = Color(0xFFEFF6FF)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val SuccessGreen = Color(0xFF10B981)
private val SuccessGreenBg = Color(0xFFECFDF5)

@Composable
fun EditUserScreen(
    navController: NavHostController,
    shareUserItemViewModel: ShareUserItemViewModel
) {
    val shareViewModel by shareUserItemViewModel.userModel.collectAsState()
    val viewmodel: EditUserViewModel = koinViewModel()

    var name by remember { mutableStateOf(shareViewModel?.name ?: "") }
    var password by remember { mutableStateOf(shareViewModel?.password ?: "") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var userAccess by remember { mutableStateOf(shareViewModel?.userAccess ?: false) }
    var adminAccess by remember { mutableStateOf(shareViewModel?.admin ?: false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Bar
                Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigateUp() },
                        color = Color.White,
                        shadowElevation = 2.dp
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

                    Text(
                        text = "Edit User Profile",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                            fontSize = 28.sp,
                            color = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Main Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Branding Logo
                        Image(
                            painter = painterResource(Res.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Waves Of Food",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                                fontSize = 26.sp,
                                color = Color.Black
                            )
                        )

                        Text(
                            text = "Modify staff credentials and permissions",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // User Name Input
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("User Name", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.usernew),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = PrimaryBlue
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder,
                                focusedLabelColor = PrimaryBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Input with Masking
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password", fontSize = 13.sp) },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.lock),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = PrimaryBlue
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(Res.drawable.eye),
                                        contentDescription = "Toggle Password",
                                        modifier = Modifier.size(18.dp),
                                        tint = TextMuted
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder,
                                focusedLabelColor = PrimaryBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Access Toggle Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AccessToggleCard(
                                title = "Staff Access",
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

                        Spacer(modifier = Modifier.height(28.dp))

                        // Update Action Button
                        Button(
                            onClick = {
                                val updatedUser = UserModel(
                                    key = shareViewModel?.key,
                                    name = name.trim(),
                                    password = password,
                                    userAccess = userAccess,
                                    admin = adminAccess
                                )

                                if (updatedUser != shareViewModel) {
                                    viewmodel.updateUser(updatedUser) {
                                        navController.navigateUp()
                                    }
                                } else {
                                    navController.navigateUp()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Save Changes",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Footer Section
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Designed & Developed by",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Hammad Baloch",
                    color = PrimaryBlue,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                )
            }
        }
    }
}

@Composable
fun AccessToggleCard(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isChecked) SuccessGreen else CardBorder,
        label = "BorderColor"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isChecked) SuccessGreenBg else Color(0xFFF8FAFC),
        label = "BgColor"
    )

    Surface(
        modifier = modifier
            .border(1.5.dp, animatedBorderColor, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!isChecked) },
        color = animatedBgColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isChecked) SuccessGreen else TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SuccessGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}