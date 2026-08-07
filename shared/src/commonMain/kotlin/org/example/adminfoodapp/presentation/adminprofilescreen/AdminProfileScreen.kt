package org.example.adminfoodapp.presentation.adminprofilescreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import org.example.adminfoodapp.database.model.UserModel
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.example.adminfoodapp.utils.Resource
import org.example.adminfoodapp.utils.SessionManager
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// Theme Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryBlueBg = Color(0xFFEFF6FF)
private val AdminPurple = Color(0xFF7C3AED)
private val AdminPurpleBg = Color(0xFFF3E8FF)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)
private val DangerRed = Color(0xFFEF4444)
private val DangerRedBg = Color(0xFFFEF2F2)

@Composable
fun AdminProfileScreen(
    navController: NavHostController,
    shareUserItemViewModel: ShareUserItemViewModel
) {
    val viewmodel: AdminViewModel = koinViewModel()
    val usersState by viewmodel.state.collectAsState()
    var id by remember { mutableStateOf<String?>(null) }
    var showAlert by remember { mutableStateOf(false) }
    val sessionManager: SessionManager = koinInject()
    val currentUserId = sessionManager.getCurrentUserKey() ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Bar
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
                    text = "Staff Management",
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

            // User States Handling
            when (val state = usersState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                is Resource.Success -> {
                    val data = state.data

                    if (data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Sub-Users Found",
                                    color = TextMuted,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(data) { user ->
                                UserItem(
                                    user = user,
                                    onDelete = {
                                        id = user.key
                                        showAlert = true
                                    },
                                    onEdit = {
                                        shareUserItemViewModel.setUserModel(user)
                                        navController.navigate(NavRoutes.EditUserScreen)
                                    },
                                    currentUserId = currentUserId
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = DangerRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Footer Section
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
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
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )
                }
            }
        }
    }

    // Modern Delete Dialog
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Delete Staff Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this user? They will lose access to the system immediately.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewmodel.deleteConfirm(id)
                        id = null
                        showAlert = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete User", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        id = null
                        showAlert = false
                    }
                ) {
                    Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun UserItem(
    user: UserModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    currentUserId: String
) {
    val canModify = user.admin == false && user.key != currentUserId
    val isAdmin = user.admin == true

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar Container
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = if (isAdmin) AdminPurpleBg else PrimaryBlueBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.usernew),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isAdmin) AdminPurple else PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name.orEmpty().ifEmpty { "Unnamed User" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Role Badge Chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAdmin) AdminPurpleBg else Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.Shield else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isAdmin) AdminPurple else TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAdmin) "Admin" else "Staff User",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAdmin) AdminPurple else TextMuted
                        )
                    }
                }
            }

            // Actions or Protected State
            if (canModify) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onEdit() },
                        color = PrimaryBlueBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(Res.drawable.edit),
                                contentDescription = "Edit",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onDelete() },
                        color = DangerRedBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            } else {
                // Protected Root Admin Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Protected",
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Protected",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}