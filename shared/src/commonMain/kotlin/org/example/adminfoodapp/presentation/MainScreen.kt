package org.example.adminfoodapp.presentation

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
fun MainScreen() {

val scrollState = rememberScrollState()
val bgColor = Brush.verticalGradient(
    colors = listOf(Color(0xFFF8F9FE), Color(0xFFE9EFFF))
)
Box(
modifier = Modifier
.fillMaxSize()
.background(bgColor)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Top Bar
        TopAppBarSection()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome back, Admin 👋",
            color = Color(0xFF808191),
            fontSize = 14.sp
        )
        // Stats Section
        Spacer(modifier = Modifier.height(20.dp))
        StatsSection()
        // Quick Actions Section
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Quick Management",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                fontSize = 20.sp,
                color = Color(0xFF11142D)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        QuickActionsGrid()
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Design By\nHammad Baloch",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color(0xFF808191),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                lineHeight = 22.sp
            )
        )
        Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB
    }
    // Floating Action Button
    FloatingActionButton(
        onClick = { /* Sign Out */ },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        containerColor = Color(0xFFFF3B3B),
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
    }
}
}
@Composable
fun TopAppBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(45.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = Color(0xFF11142D)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "X Cafe",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                color = Color(0xFF11142D)
            )
        )
    }
}
@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Earning Card
        Card(
            modifier = Modifier.weight(0.45f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6C5DD3)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                ) {
                    // Replace with your dollar icon
                    Text("$", modifier = Modifier.wrapContentSize(), fontWeight = FontWeight.Bold, color = Color(0xFF6C5DD3))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("₹ 150,000", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Whole Time\nEarnings", color = Color(0xFFE4E1FA), fontSize = 12.sp)
            }
        }
        // Pending and Processed Cards
        Column(
            modifier = Modifier.weight(0.55f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatSmallCard(title = "Pending", value = "12", valueColor = Color(0xFFFF9800), bgColor = Color(0xFFFFF4E5))
            StatSmallCard(title = "Processed", value = "45", valueColor = Color(0xFF4CAF50), bgColor = Color(0xFFEAF7ED))
        }
    }
}
@Composable
fun StatSmallCard(title: String, value: String, valueColor: Color, bgColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().height(75.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color(0xFF808191), fontSize = 14.sp)
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun QuickActionsGrid() {
    val items = listOf(
        QuickActionData("Add Menu\nItem", Color(0xFFE8F5E9), Color(0xFF2E7D32)),
        QuickActionData("All Items\nCatalog", Color(0xFFE3F2FD), Color(0xFF1565C0)),
        QuickActionData("Processed\nOrders", Color(0xFFFFF8E1), Color(0xFFF9A825)),
        QuickActionData("New\nOrders", Color(0xFFFFF3E0), Color(0xFFEF6C00)),
        QuickActionData("Admin\nProfile", Color(0xFFF3E5F5), Color(0xFF6A1B9A)),
        QuickActionData("Create New\nSub-User", Color(0xFFFFF8E1), Color(0xFFF9A825))
    )
    // Using BoxWithConstraints to decide grid columns
    BoxWithConstraints {
        val columns = if (maxWidth > 600.dp) 3 else 2

        // Manual Grid because LazyVerticalGrid inside Scrollable Column is tricky
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (i in items.indices step columns) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (j in 0 until columns) {
                        val index = i + j
                        if (index < items.size) {
                            QuickActionCard(items[index], Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
data class QuickActionData(val title: String, val iconBg: Color, val iconTint: Color)
@Composable
fun QuickActionCard(data: QuickActionData, modifier: Modifier) {
    Card(
        modifier = modifier.height(130.dp).clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = data.iconBg
            ) {
                Icon(
                    Icons.Default.Add, // Replace with specific icons
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = data.iconTint
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    color = Color(0xFF11142D),
                    lineHeight = 18.sp
                )
            )
        }
    }
}