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
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

// Data Model with Status for Firebase Toggle
data class SubUser(
    val id: String, 
    val name: String, 
    val role: String, 
    var isActive: Boolean // Toggle state
)

@Composable
fun AdminProfileScreen(onBack: () -> Unit = {}) {
    // Ye list ViewModel se aani chahiye taake state persist rahe
    var subUsers by remember { 
        mutableStateOf(
            listOf(
                SubUser("1", "John Doe", "Manager", true),
                SubUser("2", "Jane Smith", "Editor", false),
                SubUser("3", "Alex Brown", "Viewer", true)
            )
        ) 
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Back Button
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            // Screen Title
            Text(
                text = "Admin Profile",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 40.sp,
                    color = Color.Black
                )
            )

            // Sub-Users List
            if (subUsers.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No Sub-Users Found", color = TextColor, fontSize = 18.sp, fontFamily = FontFamily(Font(Res.font.yeonsung_regular)))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subUsers) { user ->
                        UserItem(
                            user = user,
                            onToggleActive = { newValue ->
                                // YAHAN FIREBASE UPDATE KI LOGIC AAYEGI
                                // Example: firebase.database().ref("users").child(user.id).child("isActive").setValue(newValue)
                                
                                // UI update karne ke liye state change karein
                                subUsers = subUsers.map { 
                                    if (it.id == user.id) it.copy(isActive = newValue) else it 
                                }
                            }
                        )
                    }
                }
            }

            // Save Information Button
            Button(
                onClick = { /* Overall Save Logic */ },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TextColor)
            ) {
                Text("Save Information", color = TextColor, fontSize = 18.sp, fontFamily = FontFamily(Font(Res.font.yeonsung_regular)))
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
}

@Composable
fun UserItem(user: SubUser, onToggleActive: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FE)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Icon
            Surface(
                modifier = Modifier.size(45.dp),
                shape = CircleShape,
                color = TextColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.logo), // Use profile icon
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = TextColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(user.role, fontSize = 14.sp, color = Color.Gray)
            }

            // Status Toggle Switch
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (user.isActive) "Active" else "Blocked",
                    fontSize = 10.sp,
                    color = if (user.isActive) Color(0xFF2E7D32) else Color.Red
                )
                Switch(
                    checked = user.isActive,
                    onCheckedChange = { onToggleActive(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2E7D32),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
