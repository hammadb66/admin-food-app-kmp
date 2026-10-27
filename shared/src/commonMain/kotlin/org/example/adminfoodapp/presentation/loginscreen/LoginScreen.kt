package org.example.adminfoodapp.presentation.loginscreen

import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.lock
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.mail
import adminfoodapp.shared.generated.resources.user
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.utils.LoginState
import org.example.adminfoodapp.utils.ToastManager
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val DarkBackground = Color(0xFF060404)
private val CardBackground = Color(0x0FFFFFFF) // Semi-transparent Glass effect
private val BorderColor = Color(0x1AFFFFFF)
private val PrimaryAccent = Color(0xFFD4832E)
private val AccentGradientEnd = Color(0xFFC47020)

@Composable
fun LoginScreen(navController: NavHostController) {
    val viewModel: LoginViewModel = koinViewModel()
    val toastManager: ToastManager = koinInject()
    val state by viewModel.stateLogin.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (val result = state) {
            is LoginState.Success -> {
                isLoading = false
                if (result.data.admin == true || result.data.userAccess == true) {
                    navController.navigate(NavRoutes.MainScreen) {
                        popUpTo(NavRoutes.LoginScreen) { inclusive = true }
                    }
                } else {
                    toastManager.show("You Don't Have Access")
                }
            }

            is LoginState.Error -> {
                isLoading = false
                toastManager.show(result.message)
            }

            is LoginState.Loading -> {
                isLoading = true
            }

            else -> {}
        }
    }

    var usernameField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground)
    ) {
        // Glowing Ambient Orbs background
        Box(
            modifier = Modifier.size(350.dp).offset(x = (-100).dp, y = (-50).dp).background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55B43C14), Color.Transparent)
                ), shape = CircleShape
            )
        )
        Box(
            modifier = Modifier.size(300.dp).align(Alignment.BottomEnd).offset(x = 80.dp, y = 80.dp).background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x40D4832E), Color.Transparent)
                ), shape = CircleShape
            )
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxWidth = maxWidth
            val isDesktop = maxWidth > 750.dp

            Box(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDesktop) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f).padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.weight(1.1f), contentAlignment = Alignment.Center) {
                            LoginUpperContainer()
                        }

                        // Vertical Glow Line
                        Spacer(
                            modifier = Modifier.width(1.dp).height(380.dp).background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent, PrimaryAccent.copy(alpha = 0.4f), Color.Transparent
                                    )
                                )
                            )
                        )

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            LoginCardWrapper {
                                LoginBottomContainer(
                                    isLoading = isLoading,
                                    onButtonClick = { viewModel.login(usernameField, passwordField) },
                                    email = usernameField,
                                    onEmailChange = { usernameField = it },
                                    password = passwordField,
                                    onPasswordChange = { passwordField = it },
                                    isPasswordVisible = isPasswordVisible,
                                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                                    onDevLogin = { e, p -> viewModel.loginAsDeveloper(e, p) }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        LoginUpperContainer()
                        Spacer(modifier = Modifier.height(32.dp))
                        LoginCardWrapper {
                            LoginBottomContainer(
                                isLoading = isLoading,
                                onButtonClick = { viewModel.login(usernameField, passwordField) },
                                email = usernameField,
                                onEmailChange = { usernameField = it },
                                password = passwordField,
                                onPasswordChange = { passwordField = it },
                                isPasswordVisible = isPasswordVisible,
                                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                                onDevLogin = { e, p -> viewModel.loginAsDeveloper(e, p) })
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LoginCardWrapper(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().shadow(40.dp, shape = RoundedCornerShape(28.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(28.dp)).background(CardBackground)
            .border(1.dp, BorderColor, RoundedCornerShape(28.dp)).padding(28.dp)
    ) {
        content()
    }
}

@Composable
fun LoginBottomContainer(
    isLoading: Boolean,
    onButtonClick: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onDevLogin:(String,String)->Unit
) {
    var showDevLoginDialog by remember { mutableStateOf(false) }
    var devEmail by remember { mutableStateOf("") }
    var devPassword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome back", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5EDE0)
        )
        Text(
            text = "Sign in to access the admin panel",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("User Name") },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.user),
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = PrimaryAccent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.lock),
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        color = PrimaryAccent.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = PrimaryAccent
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Submit Button with Gradient
        Button(
            onClick = onButtonClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = PrimaryAccent.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryAccent, AccentGradientEnd)
                    )
                ), contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0C0A07)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = { showDevLoginDialog = true }) {
            Text("Login as Developer", color = PrimaryAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Custom Footer Credit
        Text(
            text = "Design By\nHammad Baloch",
            textAlign = TextAlign.Center,
            color = PrimaryAccent.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 18.sp, lineHeight = 22.sp
            )
        )
    }

    if (showDevLoginDialog) {
        AlertDialog(
            onDismissRequest = { showDevLoginDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("Developer Firebase Login", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = devEmail,
                        onValueChange = { devEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = devPassword,
                        onValueChange = { devPassword = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDevLogin(devEmail, devPassword)
                    showDevLoginDialog = false
                }) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDevLoginDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}




@Composable
fun LoginUpperContainer() {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Logo Badge
        Box(
            modifier = Modifier.size(140.dp).background(
                brush = Brush.radialGradient(
                    colors = listOf(PrimaryAccent.copy(alpha = 0.25f), Color.Transparent)
                ), shape = CircleShape
            ).border(1.dp, PrimaryAccent.copy(alpha = 0.3f), CircleShape).padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Admin Of App", color = Color(0xFFF5EDE0), style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontWeight = FontWeight.Bold, fontSize = 38.sp
            )
        )

        // Divider Line
        Box(
            modifier = Modifier.padding(vertical = 8.dp).width(60.dp).height(2.dp).background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PrimaryAccent, Color.Transparent)
                )
            )
        )

        Text(
            text = "Login To Your Account", color = PrimaryAccent, style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 22.sp
            )
        )
    }
}