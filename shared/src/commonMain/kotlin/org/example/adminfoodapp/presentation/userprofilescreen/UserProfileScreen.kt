package org.example.adminfoodapp.presentation.userprofilescreen

import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.arrow_left
import adminfoodapp.shared.generated.resources.eye
import adminfoodapp.shared.generated.resources.lock
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.usernew
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import org.example.adminfoodapp.utils.SessionManager
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// Modern Theme Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val DangerRed = Color(0xFFEF4444)
private val DangerRedBg = Color(0xFFFEF2F2)

@Composable
fun UserProfileScreen(navController: NavHostController) {
    val viewModel: UserProfileViewModel = koinViewModel()
    val currentUserState by viewModel.state.collectAsState()
    val sessionManager: SessionManager = koinInject()
    var currentPassword by remember { mutableStateOf("") }
    val currentKey = sessionManager.getCurrentUserKey()

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf(false) }
    var userAccess by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showReAuthDialog by remember { mutableStateOf(false) }
    var reAuthPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorPasswordMismatchException by remember { mutableStateOf<String?>(null) }
    val updateState by viewModel.stateUpdate.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(currentUserState) {
        when (val state = currentUserState) {
            is Resource.Success -> {
                val data = state.data
                data.name?.let { name = it }
                data.password?.let { password = it }
                data.password?.let { currentPassword = it }
                data.admin?.let { admin = it }
                data.userAccess?.let { userAccess = it }
            }
            is Resource.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }

    LaunchedEffect(updateState) {
        when (val state = updateState) {
            is Resource.Success -> {
                viewModel.resetValue()
                navController.navigateUp()
            }
            is Resource.Error -> {
                errorMessage = state.message
            }
            else -> {}
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
                Spacer(modifier = Modifier.height(16.dp).statusBarsPadding())

                // Profile Card Container
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
                        Image(
                            painter = painterResource(Res.drawable.logo),
                            contentDescription = "Profile Logo",
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
                            text = "Update your account details",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Error Alert Banner
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // User Name Input
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Name Of User", fontSize = 13.sp) },
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

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { if (it.length <= 12) password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password", fontSize = 13.sp) },
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
                                        contentDescription = "Toggle Visibility",
                                        modifier = Modifier.size(18.dp),
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder,
                                focusedLabelColor = PrimaryBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Update Trigger Button
                        Button(
                            onClick = { showReAuthDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Update Profile",
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

        // Global Loading Overlay
        if (currentUserState is Resource.Loading || updateState is Resource.Loading) {
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Please wait...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Security Re-Authentication Modal Dialog
        if (showReAuthDialog) {
            AlertDialog(
                onDismissRequest = {
                    errorPasswordMismatchException = null
                    reAuthPassword = ""
                    showReAuthDialog = false
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Security Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Please confirm your current password to save these profile changes.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )

                        errorPasswordMismatchException?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = err,
                                color = DangerRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = reAuthPassword,
                            onValueChange = { reAuthPassword = it },
                            label = { Text("Current Password", fontSize = 12.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (currentPassword == reAuthPassword) {
                                viewModel.updateUser(
                                    UserModel(
                                        key = currentKey,
                                        name = name.trim(),
                                        password = password,
                                        admin = admin,
                                        userAccess = userAccess
                                    )
                                )
                                errorPasswordMismatchException = null
                                showReAuthDialog = false
                                reAuthPassword = ""
                            } else {
                                errorPasswordMismatchException = "Password does not match"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            errorPasswordMismatchException = null
                            showReAuthDialog = false
                            reAuthPassword = ""
                        }
                    ) {
                        Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}