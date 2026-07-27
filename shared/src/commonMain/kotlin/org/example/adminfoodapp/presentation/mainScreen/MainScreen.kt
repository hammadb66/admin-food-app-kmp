package org.example.adminfoodapp.presentation.mainScreen

import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MainScreen(navController: NavHostController) {
    val viewModel: MainScreenViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }


    val bgColor = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8F9FE), Color(0xFFE9EFFF))
    )

    Box(
        modifier = Modifier.fillMaxSize().background(bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp)
        ) {
            // Top Bar
            TopAppBarSection()

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome back, Admin 👋", color = Color(0xFF808191), fontSize = 14.sp
            )

            // Stats Section
            Spacer(modifier = Modifier.height(20.dp))
            StatsSection(
                onTotalEarningClick = {
                    showDialog = true
                },
                totalEarningCount = uiState.totalEarnings,
                pendingOrderCount = uiState.pendingOrdersCount,
                processedOrderCount = uiState.completeOrdersCount
            )

            // Quick Actions Section
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Quick Management", style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 20.sp,
                    color = Color(0xFF11142D)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            QuickActionsGrid(navController = navController)

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Design By\nHammad Baloch",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF808191),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                scope.launch {
                    viewModel.sessionOut()
                    navController.navigate(NavRoutes.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }

                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = Color(0xFFFF3B3B),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(painter = painterResource(Res.drawable.log_out), contentDescription = "Sign Out")
        }
    }

    if (showDialog) {
        EarningsDialog(amount = uiState.totalEarnings, onDismiss = { showDialog = false })
    }


}

@Composable
fun TopAppBarSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(45.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Icon(
                painterResource(Res.drawable.profile_user),
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = Color(0xFF11142D)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "X Cafe", style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), color = Color(0xFF11142D)
            )
        )
    }
}


@Composable
fun StatsSection(
    onTotalEarningClick: () -> Unit, totalEarningCount: Int, pendingOrderCount: Int, processedOrderCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Earning Card
        Card(
            modifier = Modifier.weight(0.45f).fillMaxHeight().clickable(onClick = {
                onTotalEarningClick()
            }),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6C5DD3)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp), color = Color.White, modifier = Modifier.size(32.dp)
                ) {
                    // Replace with your dollar icon
                    Icon(
                        painterResource(Res.drawable.dollar),
                        contentDescription = "$",
                        modifier = Modifier.wrapContentSize(),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Rs: $totalEarningCount", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                )
                Text("Whole Time\nEarnings", color = Color(0xFFE4E1FA), fontSize = 12.sp)
            }
        }

        // Pending and Processed Cards
        Column(
            modifier = Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatSmallCard(
                title = "Pending",
                value = "$pendingOrderCount",
                valueColor = Color(0xFFFF9800),
                bgColor = Color(0xFFFFF4E5)
            )
            StatSmallCard(
                title = "Processed",
                value = "$processedOrderCount",
                valueColor = Color(0xFF4CAF50),
                bgColor = Color(0xFFEAF7ED)
            )
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
                color = bgColor, shape = RoundedCornerShape(8.dp)
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
fun QuickActionsGrid(navController: NavController) {
    val items = listOf(
        QuickActionData(
            "Add Menu\nItem", Color(0xFFE8F5E9), Color(0xFF2E7D32), Res.drawable.pluse_round, NavRoutes.AddItemScreen
        ), QuickActionData(
            "All Items\nCatalog", Color(0xFFE3F2FD), Color(0xFF1565C0), Res.drawable.eye, NavRoutes.AllItemScreen
        ), QuickActionData(
            "Processed\nOrders",
            Color(0xFFFFF8E1),
            Color(0xFFF9A825),
            Res.drawable.order_processed,
            NavRoutes.OutOfDeliveryScreen
        ), QuickActionData(
            "New\nOrders", Color(0xFFFFF3E0), Color(0xFFEF6C00), Res.drawable.attantion, NavRoutes.PendingOrderScreen
        ), QuickActionData(
            "Admin\nProfile", Color(0xFFF3E5F5), Color(0xFF6A1B9A), Res.drawable.user, NavRoutes.AdminProfileScreen
        ), QuickActionData(
            "Create New\nSub-User",
            Color(0xFFFFF8E1),
            Color(0xFFF9A825),
            Res.drawable.user_plus,
            NavRoutes.CreateUserScreen
        )
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
                            QuickActionCard(
                                navController = navController, data = items[index], modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(navController: NavController, data: QuickActionData, modifier: Modifier) {
    Card(
        modifier = modifier.height(130.dp).clickable {
            navController.navigate(data.navRoutes)
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), color = data.iconBg
            ) {
                Icon(
                    painterResource(data.iconRes),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = data.iconTint
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = data.title, style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    color = Color(0xFF11142D),
                    lineHeight = 18.sp
                )
            )
        }
    }
}


data class QuickActionData(
    val title: String, val iconBg: Color, val iconTint: Color, val iconRes: DrawableResource, val navRoutes: NavRoutes
)
