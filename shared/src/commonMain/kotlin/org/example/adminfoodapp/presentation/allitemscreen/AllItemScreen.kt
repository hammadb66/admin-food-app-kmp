package org.example.adminfoodapp.presentation.allitemscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

// Data Model for Menu Item
data class MenuItem(
    val id: String,
    val name: String,
    val price: String,
    val imageRes: Int = 0 // In real app, this will be a URL string
)

@Composable
fun AllItemScreen() {
    // Dummy Items List
    val menuItems = listOf(
        MenuItem("1", "Spacy fresh crab", "$15"),
        MenuItem("2", "Burger Special", "$10"),
        MenuItem("3", "Italian Pasta", "$20"),
        MenuItem("4", "Fresh Salad", "$8")
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable {  }
            )

            Text(
                text = "All Items",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 40.sp,
                    color = Color.Black
                )
            )

            // Items List or Empty State
            if (menuItems.isEmpty()) {
                EmptyState(onAddItem = {  })
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(menuItems) { item ->
                        MenuListItem(
                            item = item,
                            onEdit = { /* Edit Logic */ },
                            onDelete = { /* Delete Logic */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuListItem(item: MenuItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(75.dp),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFFF0F0F0)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo), // Use menu1 placeholder
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.price,
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 20.sp,
                    color = TextColor
                )
            }

            // Actions (Edit/Delete)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit) {
                    Icon(
                       painter = painterResource(Res.drawable.edit),
                        contentDescription = "Edit",
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(Res.drawable.trash),
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(onAddItem: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Menu Items Found",
            color = TextColor,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = onAddItem,
            modifier = Modifier.width(160.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text(
                "Add Item",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
            )
        }
    }
}
