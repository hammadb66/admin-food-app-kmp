package org.example.adminfoodapp.presentation.edititemscreen

import adminfoodapp.shared.generated.resources.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.example.adminfoodapp.data.model.AllMenu
import org.example.adminfoodapp.presentation.viewmodel.SharedEditItemViewModel
import org.example.adminfoodapp.utils.UploadState
import org.example.adminfoodapp.utils.rememberBitmapFromBytes
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import coil3.compose.AsyncImage

@Composable
fun EditItemScreen(navController: NavHostController, sharedEditItemViewModel: SharedEditItemViewModel) {

    val previousItemState = sharedEditItemViewModel.itemToEdit.collectAsState()
    val previousItem = previousItemState.value
    var foodName by remember { mutableStateOf(previousItem?.foodName ?: "") }
    var foodPrice by remember { mutableStateOf(previousItem?.foodPrice ?: "") }
    var description by remember { mutableStateOf(previousItem?.foodDescription ?: "") }
    var ingredients by remember { mutableStateOf(previousItem?.foodIngredient ?: "") }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageUrl by remember(previousItem) { mutableStateOf(previousItem?.foodImage) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val viewModel: EditItemViewModel = koinViewModel()

    val state by viewModel.uploadState.collectAsState()

    val scrollState = rememberScrollState()
    val goldAccent = Color(0xFFD4AF37)
    val darkGray = Color(0xFF333333)
    val scope = rememberCoroutineScope()

    val singleImagePicker = rememberFilePickerLauncher(
        type = PickerType.Image, mode = PickerMode.Single
    ) { file ->
        file?.let {
            scope.launch {
                selectedImageBytes = it.readBytes()
            }
        }
    }
    LaunchedEffect(state) {
        when (state) {
            is UploadState.Success -> {
                viewModel.resetState()
                navController.navigateUp()

            }

            is UploadState.Error -> {
                errorMessage = (state as UploadState.Error).message

            }

            else -> Unit


        }


    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp)
            ) {
                // Background Image Placeholder
                AsyncImage(
                        model = selectedImageBytes?:imageUrl?:Res.drawable.image_placeholder,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )


                // Top Gradient Scrim
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
                )

                // Back Button & Title
                Row(
                    modifier = Modifier.padding(16.dp).statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(45.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(painterResource(Res.drawable.arrow_left), contentDescription = "Back", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "New Menu Item",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )
                }

                // Floating Change Image Button
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp, end = 20.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { singleImagePicker.launch() },
                        containerColor = goldAccent,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(55.dp)
                    ) {
                        Icon(painterResource(Res.drawable.pluse_round), contentDescription = "Add Image")
                    }
                }
            }

            // Form Sheet Section
            Surface(
                modifier = Modifier.fillMaxWidth().offset(y = (-30).dp), // Overlap effect
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White, tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier.width(50.dp).height(5.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Form Fields
                    AddItemTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = "Item Name",
                        accentColor = goldAccent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddItemTextField(
                        value = foodPrice,
                        onValueChange = { foodPrice = it },
                        label = "Price",
                        accentColor = goldAccent,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Description",
                        color = darkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("A short, elegant description") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = goldAccent, focusedLabelColor = goldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Ingredients",
                        color = darkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.yeonsung_regular))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { ingredients = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Noodle, Salad, Burger...") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = goldAccent, focusedLabelColor = goldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Add Item Button
                    Button(
                        onClick = {
                            if (foodName.isBlank() || foodPrice.isBlank() || description.isBlank() || ingredients.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }

                            errorMessage = null
                            val item = AllMenu(
                                key = previousItem?.key,
                                foodName = foodName.trim(),
                                foodPrice = foodPrice.trim(),
                                foodDescription = description.trim(),
                                foodIngredient = ingredients.trim(),
                                foodImage = imageUrl
                            )
                            sharedEditItemViewModel.clearItemToEdit()
                            viewModel.updateMenuItem(item, selectedImageBytes)
                        },
                        modifier = Modifier.fillMaxWidth().height(65.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text(
                            text = "Update Item",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.yeonsung_regular)),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        if (state is UploadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = goldAccent)
            }
        }
    }
}

@Composable
fun AddItemTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    accentColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor, focusedLabelColor = accentColor
        )
    )
}
