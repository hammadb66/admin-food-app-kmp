package org.example.adminfoodapp.presentation.loginscreen

import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.lock
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.mail
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(navController: NavHostController) {
    // State ko yahan rakha hai taake resize par data na ure (State Hoisting)
    var emailField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = maxWidth
        val isDesktop = maxWidth > 700.dp

        // Pura content scrollable Box mein rakha hai
        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isDesktop) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f).padding(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        LoginUpperContainer()
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        LoginBottomContainer(
                            email = emailField,
                            onEmailChange = { emailField = it },
                            password = passwordField,
                            onPasswordChange = { passwordField = it })
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
                    Spacer(modifier = Modifier.height(40.dp))
                    LoginBottomContainer(
                        email = emailField,
                        onEmailChange = { emailField = it },
                        password = passwordField,
                        onPasswordChange = { passwordField = it })
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun LoginBottomContainer(
    email: String, onEmailChange: (String) -> Unit, password: String, onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email Address") },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.mail),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextColor, focusedLabelColor = TextColor, cursorColor = TextColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = { Icon(painter = painterResource(Res.drawable.lock), contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextColor, focusedLabelColor = TextColor, cursorColor = TextColor
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { /* Login Action */ },
            modifier = Modifier.fillMaxWidth(0.8f).height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TextColor)
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Design By\nHammad Baloch",
            textAlign = TextAlign.Center,
            color = TextColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 18.sp, lineHeight = 24.sp
            )
        )
    }
}

@Composable
fun LoginUpperContainer() {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Admin Of App", style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = "Login To Your Account", color = TextColor, style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 24.sp
            )
        )
    }
}
