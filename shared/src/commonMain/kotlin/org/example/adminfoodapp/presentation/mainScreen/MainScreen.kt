package org.example.adminfoodapp.presentation.mainScreen

import adminfoodapp.shared.generated.resources.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.OrderDetails
import org.example.adminfoodapp.database.model.QuickPendingOrder
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.additemscreen.AddItemScreen
import org.example.adminfoodapp.presentation.adminprofilescreen.AdminProfileScreen
import org.example.adminfoodapp.presentation.allitemscreen.AllItemScreen
import org.example.adminfoodapp.presentation.createuserscreen.CreateUserScreen
import org.example.adminfoodapp.presentation.dineinscreen.TableManagementScreen
import org.example.adminfoodapp.presentation.generalhistoryscreen.OrderHistoryScreen
import org.example.adminfoodapp.presentation.outofdeliveryscreen.OutOfDeliveryScreen
import org.example.adminfoodapp.presentation.pendingorderscreen.PendingOrderScreen
import org.example.adminfoodapp.presentation.tableanditemsscreen.TableAndItemsScreen
import org.example.adminfoodapp.presentation.takeawayscreen.TakeAwayScreen
import org.example.adminfoodapp.presentation.userprofilescreen.UserProfileScreen
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.example.adminfoodapp.presentation.viewmodel.SharedEditItemViewModel
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel
import org.example.adminfoodapp.utils.MainActivityUiState
import org.example.adminfoodapp.utils.ORDERTYPE
import org.example.adminfoodapp.utils.SessionManager
import org.example.adminfoodapp.utils.isDesktopPlatform
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val SurfaceBackground = Color(0xFFF8FAFC)
private val SidebarBackground = Color(0xFFFFFFFF)
private val PrimaryAccent = Color(0xFF059669)
private val PrimaryAccentLight = Color(0xFFECFDF5)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val CardBorderColor = Color(0xFFE2E8F0)

// Data class mein Icon resource add kar diya
data class SidebarMenuItem(
    val title: String, val shortCode: String, val targetTab: DashboardTab
)

enum class DashboardTab(val title: String, val details: String) {
    DASHBOARD_HOME("Dashboard", ""), TAKEAWAY_BILLING(
        "Takeaway POS", "Quick Order Entry & Thermal Checkout"
    ),
    DINE_IN_TABLES(
        "Dine-In Management", "Manage restaurant floor & table availability"
    ),
    ONLINE_PENDING("Pending Orders", ""), GENERAL_ITEM("General Item", ""), ONLINE_HISTORY(
        "Online Delivery History", ""
    ),
    GENERAL_HISTORY("General Sales History", ""), ADD_MENU_ITEM("Add New Item", ""), ONLINE_ITEMS(
        "Menu Catalog", ""
    ),
    ADMIN_PROFILE("Admin Profile", ""), SUB_USERS("User Access Control", ""), EDIT_PROFILE("Edit Your Profile", "")
}

@Composable
fun MainScreen(
    navController: NavHostController, viewModel: MainScreenViewModel = koinViewModel()
) {
    val sessionManager: SessionManager = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val localOrders by viewModel.localPendingOrders.collectAsState()
    val onlineOrders by viewModel.onlinePendingOrders.collectAsState()

    val sharedEditItemViewModel = koinInject<SharedEditItemViewModel>()
    val sharedOrderItemViewModel = koinInject<SharedOrderItemViewModel>()
    val shareUserItemViewModel = koinInject<ShareUserItemViewModel>()
    val userName by remember { mutableStateOf(sessionManager.getCurrentUserName()) }
    var currentTab by rememberSaveable { mutableStateOf(DashboardTab.DASHBOARD_HOME) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxSize().background(SurfaceBackground)
    ) {
        ResponsiveSidebarSection(
            username = userName,
            currentTab = currentTab,
            onTabSelected = { selectedTab -> currentTab = selectedTab },
            onLogoutClick = {
                scope.launch {
                    viewModel.sessionOut()
                    navController.navigate(NavRoutes.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            })

        Box(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center), color = PrimaryAccent
                )
            } else {
                when (currentTab) {
                    DashboardTab.DASHBOARD_HOME -> {
                        DashboardHomeContent(
                            uiState = uiState,
                            localOrders = localOrders,
                            onlineOrders = onlineOrders,
                            navController = navController,
                            onNavigateToTab = { currentTab = it })
                    }

                    DashboardTab.TAKEAWAY_BILLING -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.TAKEAWAY_BILLING.title,
                            detail = DashboardTab.TAKEAWAY_BILLING.details,
                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            TakeAwayScreen(navController = navController)
                        }
                    }

                    DashboardTab.DINE_IN_TABLES -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.DINE_IN_TABLES.title, detail = DashboardTab.DINE_IN_TABLES.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            TableManagementScreen(navController = navController)
                        }
                    }

                    DashboardTab.ONLINE_PENDING -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.ONLINE_PENDING.title, detail = DashboardTab.ONLINE_PENDING.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            PendingOrderScreen(
                                navController = navController, sharedOrderItemViewModel = sharedOrderItemViewModel
                            )
                        }
                    }

                    DashboardTab.ONLINE_HISTORY -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.ONLINE_HISTORY.title, detail = DashboardTab.ONLINE_HISTORY.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            OutOfDeliveryScreen(navController = navController)
                        }
                    }

                    DashboardTab.GENERAL_HISTORY -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.GENERAL_HISTORY.title, detail = DashboardTab.GENERAL_HISTORY.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            OrderHistoryScreen(navController = navController)
                        }
                    }

                    DashboardTab.ADD_MENU_ITEM -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.ADD_MENU_ITEM.title, detail = DashboardTab.ADD_MENU_ITEM.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            AddItemScreen(navController = navController)
                        }
                    }

                    DashboardTab.ONLINE_ITEMS -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.ONLINE_ITEMS.title, detail = DashboardTab.ADD_MENU_ITEM.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            AllItemScreen(
                                navController = navController, sharedEditItemViewModel = sharedEditItemViewModel
                            )
                        }
                    }

                    DashboardTab.ADMIN_PROFILE -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.ONLINE_ITEMS.title, detail = DashboardTab.ONLINE_ITEMS.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            AdminProfileScreen(
                                navController = navController, shareUserItemViewModel = shareUserItemViewModel
                            )
                        }
                    }

                    DashboardTab.SUB_USERS -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.SUB_USERS.title, detail = DashboardTab.SUB_USERS.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            CreateUserScreen(navController = navController)
                        }
                    }

                    DashboardTab.GENERAL_ITEM -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.GENERAL_ITEM.title, detail = DashboardTab.GENERAL_ITEM.details,

                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            TableAndItemsScreen(navController = navController)
                        }
                    }

                    DashboardTab.EDIT_PROFILE -> {
                        EmbeddedScreenWrapper(
                            title = DashboardTab.EDIT_PROFILE.title,
                            detail = DashboardTab.EDIT_PROFILE.details,
                            onBackToDashboard = { currentTab = DashboardTab.DASHBOARD_HOME }) {
                            UserProfileScreen(navController = navController)

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmbeddedScreenWrapper(
    title: String, detail: String, onBackToDashboard: () -> Unit, content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary
                    )
                    Text(
                        text = detail, fontSize = 10.sp, color = TextSecondary
                    )
                }
            }
        },
            navigationIcon = {
                IconButton(onClick = onBackToDashboard) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SidebarBackground),
            modifier = Modifier.shadow(2.dp)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun ResponsiveSidebarSection(
    currentTab: DashboardTab, onTabSelected: (DashboardTab) -> Unit, onLogoutClick: () -> Unit, username: String
) {
    var isExpanded by remember { mutableStateOf(true) }
    if (!isDesktopPlatform()){
        isExpanded=false
    }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 260.dp else 80.dp, animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    // Icons ki bajaye Short Codes define kiye hain
    val allMenuItems = listOf(
        SidebarMenuItem("Dashboard", "DB", DashboardTab.DASHBOARD_HOME),
        SidebarMenuItem("Takeaway POS", "POS", DashboardTab.TAKEAWAY_BILLING),
        SidebarMenuItem("Dine-In Tables", "DT", DashboardTab.DINE_IN_TABLES),
        SidebarMenuItem("Online Pending", "OP", DashboardTab.ONLINE_PENDING),
        SidebarMenuItem("Online History", "OH", DashboardTab.ONLINE_HISTORY),
        SidebarMenuItem("General History", "GH", DashboardTab.GENERAL_HISTORY),
        SidebarMenuItem("Add Menu Item", "AM", DashboardTab.ADD_MENU_ITEM),
        SidebarMenuItem("Online Items", "OI", DashboardTab.ONLINE_ITEMS),
        SidebarMenuItem("General Items", "GI", DashboardTab.GENERAL_ITEM),
        SidebarMenuItem("Admin Profile", "AP", DashboardTab.ADMIN_PROFILE),
        SidebarMenuItem("Sub Users", "SU", DashboardTab.SUB_USERS)
    )

    val menuItems = remember {
        if (!isDesktopPlatform()) {
            allMenuItems.filter { item ->
                item.targetTab != DashboardTab.TAKEAWAY_BILLING && item.targetTab != DashboardTab.DINE_IN_TABLES && item.targetTab != DashboardTab.GENERAL_HISTORY && item.targetTab != DashboardTab.GENERAL_ITEM
            }
        } else {
            allMenuItems
        }
    }
    Column(
        modifier = Modifier.width(sidebarWidth).fillMaxHeight().background(SidebarBackground)
            .border(width = 1.dp, color = CardBorderColor).padding(12.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Profile Header & Collapse Toggle Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                modifier = Modifier.size(46.dp).clip(CircleShape).clickable {
                                    onTabSelected(
                                        DashboardTab.EDIT_PROFILE
                                    )
                                }, color = SidebarBackground, shadowElevation = 4.dp
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.profile_user),
                                    contentDescription = "Profile",
                                    modifier = Modifier.padding(11.dp),
                                    tint = PrimaryAccent
                                )
                            }
                        }
                    }
                    if (isExpanded) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = username,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(28.dp), enabled = isDesktopPlatform()
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                        contentDescription = "Toggle Sidebar",
                        tint = TextSecondary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

            // Menu Items List (Initials Badge Rendered)
            menuItems.forEach { item ->
                val isSelected = currentTab == item.targetTab
                val itemBg = if (isSelected) PrimaryAccent else Color.Transparent
                val itemContentColor = if (isSelected) Color.White else TextPrimary
                val badgeBg = if (isSelected) Color.White.copy(alpha = 0.2f) else Color(0xFFF1F5F9)
                val badgeTextColor = if (isSelected) Color.White else PrimaryAccent

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(12.dp))
                    .background(itemBg).clickable { onTabSelected(item.targetTab) }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center) {
                    // Icon ki jagah Text Initials Badge
                    Surface(
                        modifier = Modifier.size(28.dp), shape = RoundedCornerShape(8.dp), color = badgeBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.shortCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeTextColor
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = itemContentColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout Button
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFEF2F2))
                .clickable { onLogoutClick() }.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(28.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFFFEE2E2)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "LO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)
                    )
                }
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.width(10.dp))
                Text("Logout", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DashboardHomeContent(
    uiState: MainActivityUiState,
    localOrders: List<QuickPendingOrder>,
    onlineOrders: List<OrderDetails>,
    navController: NavHostController,
    onNavigateToTab: (DashboardTab) -> Unit
) {
    val dineInPending = localOrders.filter {
        it.orderType.equals(ORDERTYPE.DINEIN.id) || it.titleOrTable.contains(
            "Table", ignoreCase = true
        )
    }
    val takeawayPending = localOrders.filter { !dineInPending.contains(it) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome Back 👋", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium
                )
                Text(
                    text = "X Cafe POS Overview", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        DetailedStatsGrid(uiState = uiState)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Active Live Orders & Lists", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary
        )
        Text(
            text = "Real-time tracking for Online, Takeaway and Dine-In orders", fontSize = 12.sp, color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Orders Section (Old Row ko is BoxWithConstraints se replace karein):
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isSingleColumn = maxWidth < 720.dp

            if (isSingleColumn) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OnlinePendingOrdersCard(orders = onlineOrders, modifier = Modifier.fillMaxWidth())

                    SpecificPendingOrdersCard(
                        title = "Pending Takeaway Orders",
                        orders = takeawayPending,
                        badgeColor = Color(0xFF9333EA),
                        bgColor = Color(0xFFF3E8FF),
                        modifier = Modifier.fillMaxWidth()
                    )

                    SpecificPendingOrdersCard(
                        title = "Pending Dine-In Orders",
                        orders = dineInPending,
                        badgeColor = PrimaryAccent,
                        bgColor = PrimaryAccentLight,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OnlinePendingOrdersCard(
                            orders = onlineOrders, modifier = Modifier.weight(1f)
                        )

                        SpecificPendingOrdersCard(
                            title = "Pending Takeaway Orders",
                            orders = takeawayPending,
                            badgeColor = Color(0xFF9333EA),
                            bgColor = Color(0xFFF3E8FF),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SpecificPendingOrdersCard(
                            title = "Pending Dine-In Orders",
                            orders = dineInPending,
                            badgeColor = PrimaryAccent,
                            bgColor = PrimaryAccentLight,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DetailedStatsGrid(uiState: MainActivityUiState) {
    val statsList = listOf(
        MetricData("Total Online Revenue", "Rs. ${uiState.totalOnlineEarnings}", PrimaryAccentLight, PrimaryAccent),
        MetricData("Total Local Revenue", "Rs. ${uiState.totalLocalEarnings}", PrimaryAccentLight, PrimaryAccent),
        MetricData(
            "Online Pending Orders", "${uiState.onlinePendingOrders} Orders", Color(0xFFFFFBEB), Color(0xFFD97706)
        ),
        MetricData(
            "Local Pending Orders", "${uiState.localPendingOrders} Orders", Color(0xFFFFFBEB), Color(0xFFD97706)
        ),
        MetricData(
            "Online Completed Orders", "${uiState.onlineCompleteOrders} Orders", Color(0xFFEEF2FF), Color(0xFF4F46E5)
        ),
        MetricData(
            "Local Completed Orders", "${uiState.localCompleteOrders} Orders", Color(0xFFEEF2FF), Color(0xFF4F46E5)
        )
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth < 600.dp -> 1
            maxWidth < 900.dp -> 2
            maxWidth < 1300.dp -> 3
            else -> 6
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            statsList.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (item in rowItems) {
                        MetricCard(
                            title = item.title,
                            value = item.value,
                            iconBg = item.iconBg,
                            iconTint = item.iconTint,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class MetricData(
    val title: String, val value: String, val iconBg: Color, val iconTint: Color
)

@Composable
fun MetricCard(title: String, value: String, iconBg: Color, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SidebarBackground),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).background(iconBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.dollar),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun OnlinePendingOrdersCard(orders: List<OrderDetails>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SidebarBackground),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Online Orders",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = "${orders.size} Pending",
                        color = Color(0xFF2563EB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false, // <-- Vertical text break hone se rokega
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorderColor)
            Spacer(modifier = Modifier.height(8.dp))

            if (orders.isEmpty()) {
                Text(
                    "Koi online order pending nahi hai.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                orders.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.userName ?: "Customer",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            item.totalPrice ?: "0",
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecificPendingOrdersCard(
    title: String, orders: List<QuickPendingOrder>, badgeColor: Color, bgColor: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SidebarBackground),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = bgColor, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = "${orders.size} Pending",
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false, // <-- Vertical text break hone se rokega
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorderColor)
            Spacer(modifier = Modifier.height(8.dp))

            if (orders.isEmpty()) {
                Text(
                    "Koi order pending nahi hai.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                orders.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.titleOrTable,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Rs. ${item.totalAmount.toInt()}",
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}