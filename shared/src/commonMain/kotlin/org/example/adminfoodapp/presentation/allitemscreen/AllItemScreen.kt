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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.utils.Resource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AllItemScreen(navController: NavHostController) {
    val viewModel: MenuViewModel = koinViewModel()
    val menuState by viewModel.state.collectAsState()
    val deleteState by viewModel.stateDelete.collectAsState()

    // Delete hone ke baad automatically list refresh ho jayegi
    LaunchedEffect(deleteState) {
        if (deleteState is Resource.Success) {
            viewModel.getMenu()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(20.dp).statusBarsPadding())
            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = "Back",
                modifier = Modifier.size(28.dp).clickable { navController.navigateUp() })

            Text(
                text = "All Items",
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)), fontSize = 40.sp, color = Color.Black
                )
            )
            when (val state = menuState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TextColor)
                    }
                }

                is Resource.Success -> {
                    val menuItems = state.data

                    if (menuItems.isEmpty()) {
                        EmptyState(onAddItem = {
                            navController.navigate(NavRoutes.AddItemScreen) {
                                popUpTo(NavRoutes.MainScreen) {
                                    inclusive = false
                                }
                            }
                        })
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
                                    onDelete = { item.key?.let { viewModel.deleteMenu(it) } })
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message ?: "An error occurred", color = Color.Red, fontSize = 16.sp
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun MenuListItem(item: AllMenu, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(75.dp), shape = RoundedCornerShape(15.dp), color = Color(0xFFF0F0F0)
            ) {
                AsyncImage(
                    model = item.foodImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.logo),
                    error = painterResource(Res.drawable.logo),
                    onError = { errorState ->
                        println("Image loading failed: ${errorState.result.throwable}")
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.foodName ?: "Facing Issue",
                    fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.foodPrice ?: "Facing Issue",
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
