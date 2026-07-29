package org.example.adminfoodapp.presentation.userprofilescreen

import TextColor
import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.arrow_left
import adminfoodapp.shared.generated.resources.eye
import adminfoodapp.shared.generated.resources.lock
import adminfoodapp.shared.generated.resources.logo
import adminfoodapp.shared.generated.resources.usernew
import adminfoodapp.shared.generated.resources.yeonsung_regular
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@Composable
fun UserProfileScreen(navController: NavHostController) {

    val viewModel: UserProfileViewModel = koinViewModel()
    val currentUserState by viewModel.state.collectAsState()
    val sessionManager: SessionManager = koinInject()
    var currentPassword by remember { mutableStateOf("") }
    val currentKey = sessionManager.getCurrentUserKey()
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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


            }

            is Resource.Error -> {
                errorMessage = state.message
            }

            else -> {}
        }
    }

    LaunchedEffect(updateState){
        when(val state = updateState){
            is Resource.Success ->{
                viewModel.resetValue()
                navController.navigateUp()
            }
            is Resource.Error->{
                errorMessage = state.message
            }
            else->{}
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })
            }

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(Res.drawable.logo), // Use profile_user
                contentDescription = "Profile", modifier = Modifier.size(100.dp)
            )

            Text(
                text = "X Cafe", style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 35.sp, color = Color.Black
                )
            )

            Text(
                text = "Edit Your Profile",
                color = TextColor,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
            )
            errorMessage?.let{
                Text(text= errorMessage!!, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name Of User") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.usernew),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColor, focusedLabelColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { if (it.length <= 12) password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.lock),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.eye),
                        contentDescription = "Toggle",
                        modifier = Modifier.size(20.dp).clickable { isPasswordVisible = !isPasswordVisible })
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColor, focusedLabelColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Update Button
            Button(
                onClick = {
                    showReAuthDialog = true
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(
                    text = "Update Information",
                    color = Color.White,
                    fontSize = 18.sp,
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
        if (currentUserState is Resource.Loading) {
            errorMessage = null
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextColor)
            }
        }
        if (updateState is Resource.Loading) {
            errorMessage = null

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextColor)
            }
        }
        if (showReAuthDialog) {
            AlertDialog(onDismissRequest = {
                errorPasswordMismatchException = null
                reAuthPassword = ""
                showReAuthDialog = false
            }, title = { Text("Security Check") }, text = {
                Column {
                    Text("This operation requires recent authentication. Please enter your password to proceed.")
                    errorPasswordMismatchException?.let {
                        Text(text = errorPasswordMismatchException!!, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reAuthPassword,
                        onValueChange = { reAuthPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }, confirmButton = {
                TextButton(onClick = {
                    if (currentPassword == reAuthPassword) {
                        viewModel.updateUser(
                            UserModel(
                                key = currentKey, name = name, password = password, admin = null, userAccess = null
                            )
                        )
                        errorPasswordMismatchException = null
                        showReAuthDialog = false
                        reAuthPassword = ""
                    } else {
                        errorPasswordMismatchException = "Password doesn't Match"
                    }

                }) {
                    Text("Confirm")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    errorPasswordMismatchException = null
                    showReAuthDialog = false
                    reAuthPassword = ""
                }) {
                    Text("Cancel")
                }
            })
        }

    }
}
