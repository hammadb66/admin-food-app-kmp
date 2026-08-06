package org.example.adminfoodapp.presentation.additemscreen

import TextColor
import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.model.AllMenu
import org.example.adminfoodapp.utils.UploadState
import org.example.adminfoodapp.utils.rememberBitmapFromBytes
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Theme Colors
private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryBlueBg = Color(0xFFEFF6FF)
private val GoldAccent = Color(0xFFD4AF37)
private val CardBorder = Color(0xFFE2E8F0)
private val TextMuted = Color(0xFF64748B)

@Composable
fun AddItemScreen(
    navController: NavHostController,
) {
    val viewModel: AddItemViewModel = koinViewModel()
    val categories by viewModel.categories.collectAsState()

    var foodName by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf("") }
    var customCategoryInput by remember { mutableStateOf("") }
    var isPopular by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val uploadState by viewModel.uploadState.collectAsState()
    val scrollState = rememberScrollState()

    val singleImagePicker = rememberFilePickerLauncher(
        type = PickerType.Image, mode = PickerMode.Single
    ) { file ->
        file?.let {
            scope.launch {
                selectedImageBytes = it.readBytes()
            }
        }
    }

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                foodName = ""
                foodPrice = ""
                description = ""
                ingredients = ""
                selectedCategory = ""
                customCategoryInput = ""
                selectedImageBytes = null
                isPopular = false
                errorMessage = null
                viewModel.resetState()
                navController.navigateUp()
            }
            is UploadState.Error -> {
                errorMessage = (uploadState as UploadState.Error).message
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // 1. IMAGE UPLOADER BOX
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { singleImagePicker.launch() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, if (selectedImageBytes != null) PrimaryBlue else CardBorder)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectedImageBytes != null) {
                            Image(
                                bitmap = rememberBitmapFromBytes(selectedImageBytes!!),
                                contentDescription = "Selected Food Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                        )
                                    )
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Change Photo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = PrimaryBlueBg,
                                    shape = CircleShape,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Upload Food Image", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("PNG, JPG up to 5MB", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message Alert
                errorMessage?.let { msg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 2. CORE INFORMATION CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Basic Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Item Name
                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Item Name") },
                            placeholder = { Text("e.g. Zinger Burger") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price
                        OutlinedTextField(
                            value = foodPrice,
                            onValueChange = { foodPrice = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Price (Rs)") },
                            placeholder = { Text("e.g. 450") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Selection Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Chips Scroll
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { category ->
                                val isSelected = selectedCategory == category
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedCategory = category
                                            customCategoryInput = ""
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PrimaryBlue else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else CardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = category,
                                            color = if (isSelected) Color.White else Color.DarkGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Category Input
                        OutlinedTextField(
                            value = customCategoryInput,
                            onValueChange = {
                                customCategoryInput = it
                                if (it.isNotBlank()) selectedCategory = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Or Type New Category") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. ATTRIBUTES & DETAILS CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Item Details & Badging", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Is Popular Switch Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFBEB), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Mark as Popular Item", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("Highlight on customer home screen", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Switch(
                                checked = isPopular,
                                onCheckedChange = { isPopular = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GoldAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            label = { Text("Description") },
                            placeholder = { Text("Write short, appetizing details...") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ingredients
                        OutlinedTextField(
                            value = ingredients,
                            onValueChange = { ingredients = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            label = { Text("Ingredients") },
                            placeholder = { Text("Cheese, Patty, Sauce, Lettuce...") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = CardBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SUBMIT BUTTON
                Button(
                    onClick = {
                        if (foodName.isBlank() || foodPrice.isBlank() || description.isBlank() || ingredients.isBlank() || selectedCategory.isBlank()) {
                            errorMessage = "Please fill all fields and select a category"
                            return@Button
                        }
                        if (selectedImageBytes == null) {
                            errorMessage = "Please upload an item image"
                            return@Button
                        }

                        errorMessage = null
                        val item = AllMenu(
                            key = "",
                            foodName = foodName.trim(),
                            foodPrice = foodPrice.trim(),
                            foodDescription = description.trim(),
                            foodIngredient = ingredients.trim(),
                            foodImage = "",
                            category = selectedCategory.trim(),
                            isPopular = isPopular,
                            orderCount = 0
                        )
                        viewModel.uploadItem(item, selectedImageBytes!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Menu Item",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Loading Overlay
        if (uploadState is UploadState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Uploading Item...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}