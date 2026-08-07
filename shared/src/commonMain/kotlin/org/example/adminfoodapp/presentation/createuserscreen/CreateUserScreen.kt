package org.example.adminfoodapp.presentation.createuserscreen

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
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Theme Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val SuccessGreen = Color(0xFF10B981)
private val SuccessGreenBg = Color(0xFFECFDF5)
private val DangerRed = Color(0xFFEF4444)
private val DangerRedBg = Color(0xFFFEF2F2)

@Composable
fun CreateUserScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var userAccess by remember { mutableStateOf(false) }
    var adminAccess by remember { mutableStateOf(false) }
    val viewModel: CreateUserViewModel = koinViewModel()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val state by viewModel.signUpState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is Resource.Success -> {
                errorMessage = null
                navController.navigateUp()
            }
            is Resource.Error -> {
                errorMessage = (state as Resource.Error).message
            }
            else -> Unit
        }
    }

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
                        text = "Create Sub-User",
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

                // Main Card Form
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
                            text = "Add a new staff member to the system",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Error Banner Container
                        errorMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = DangerRedBg,
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = msg,
                                    color = DangerRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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

                        // Access Switch Options
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

                        // Submit Button
                        Button(
                            onClick = {
                                viewModel.signUpUser(
                                    user = UserModel(
                                        key = "",
                                        name = name.trim(),
                                        password = password,
                                        userAccess = userAccess,
                                        admin = adminAccess
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Create Account",
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

        // Professional Loading Overlay State
        if (state is Resource.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Creating User...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
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