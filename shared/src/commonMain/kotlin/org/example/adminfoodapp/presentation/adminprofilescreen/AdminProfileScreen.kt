package org.example.adminfoodapp.presentation.adminprofilescreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.adminfoodapp.data.model.UserModel
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.SessionManager
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminProfileScreen(navController: NavHostController, shareUserItemViewModel: ShareUserItemViewModel) {
    val viewmodel: AdminViewModel = koinViewModel()
    val usersState by viewmodel.state.collectAsState()
    var id by remember { mutableStateOf<String?>(null) }
    var showAlert by remember { mutableStateOf(false) }
    val sessionManager: SessionManager = koinInject()
    val currentUserId = sessionManager.getCurrentUserKey() ?: ""

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Back Button
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })

            // Screen Title
            Text(
                text = "Admin Profile",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 40.sp, color = Color.Black
                )
            )



            when (val state = usersState) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val data = state.data

                    if (data.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "No Sub-Users Found",
                                color = TextColor,
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(data) { user ->
                                UserItem(user = user, onDelete = {
                                    id = user.key
                                    showAlert = true
                                    println("icon $id")
                                }, onEdit = {
                                    shareUserItemViewModel.setUserModel(user)
                                    navController.navigate(NavRoutes.EditUserScreen)
                                }, currentUserId = currentUserId)
                            }
                        }
                    }

                }

                is Resource.Error -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            state.message,
                            color = TextColor,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(20.dp))
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

    if (showAlert) {
        AlertDialog(
            title = { Text(text = "Delete Alert") },
            text = { Text(text = "Are Your Sure you want to delete this entry?") },
            onDismissRequest = {
                showAlert = false
            },

            dismissButton = {
                TextButton(
                    modifier = Modifier.background(color = Color.Gray, shape = CircleShape), onClick = {
                        id = null
                        showAlert = false
                    }) {
                    Text(text = "Cancel", color = Color.White)
                }

            },
            confirmButton = {
                TextButton(
                    modifier = Modifier.background(color = Color.Red, shape = CircleShape), onClick = {
                        println("alert $id")

                        viewmodel.deleteConfirm(id)
                        id = null
                        showAlert = false
                    }) {
                    Text(text = "Delete", color = Color.White)
                }

            },
        )
    }

}

@Composable
fun UserItem(
    user: UserModel, onDelete: () -> Unit, onEdit: () -> Unit, currentUserId: String
) {
    val canModify = user.admin == false && user.key != currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FE)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // User Icon
            Surface(
                modifier = Modifier.size(45.dp), shape = CircleShape, color = TextColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.usernew),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = TextColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextColor
                )
                Text(
                    text = if (user.admin == true) "Admin" else "User", fontSize = 14.sp, color = Color.Gray
                )
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Edit Button
                IconButton(
                    enabled = canModify, onClick = { onEdit() }) {
                    Icon(
                        painter = painterResource(Res.drawable.edit),
                        contentDescription = "Edit",
                        tint = if (canModify) TextColor else Color.Gray.copy(alpha = 0.4f)
                    )
                }

                // Delete Button
                IconButton(
                    enabled = canModify, onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (canModify) Color.Red else Color.Gray.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}