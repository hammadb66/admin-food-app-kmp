package org.example.adminfoodapp.presentation.createuserscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreateUserScreen( navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Back Button
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),

                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.navigateUp() }
                )
            }

            // Logo & Title
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Waves Of Food",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 35.sp,
                    color = Color.Black
                )
            )

            Text(
                text = "Create New User",
                color = TextColor,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name Of User") },
                leadingIcon = { Icon(painter = painterResource(Res.drawable.usernew), contentDescription = null, modifier = Modifier.size(20.dp)) },
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColor,
                    focusedLabelColor = TextColor,
                    cursorColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                leadingIcon = { Icon(painter = painterResource(Res.drawable.mail), contentDescription = null, modifier = Modifier.size(20.dp)) },
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColor,
                    focusedLabelColor = TextColor,
                    cursorColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = { Icon(painter = painterResource(Res.drawable.lock), contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.eye),
                        contentDescription = "Toggle Password",
                        modifier = Modifier.size(20.dp).clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColor,
                    focusedLabelColor = TextColor,
                    cursorColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Create Button
            Button(
                onClick = { /* Create User Logic */ },
                modifier = Modifier
                    .width(180.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(
                    text = "Create New User",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                )
            }

            // Credit Text
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Design By\nHammad Baloch",
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                textAlign = TextAlign.Center,
                color = TextColor,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
            )
        }
    }
}
