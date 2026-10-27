package org.example.adminfoodapp.presentation.developersettings

import adminfoodapp.shared.generated.resources.Res
import adminfoodapp.shared.generated.resources.arrow_left
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.adminfoodapp.utils.AppMode
import org.example.adminfoodapp.utils.FileExporter
import org.example.adminfoodapp.utils.FileImporter
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val BackgroundColor = Color(0xFF0F172A)
private val CardBg = Color(0xFF1E293B)
private val CardBorder = Color(0xFF334155)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentAmber = Color(0xFFF59E0B)
private val DangerRed = Color(0xFFEF4444)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun DeveloperSettingsScreen(navController: NavHostController) {
    val viewModel: DeveloperSettingsViewModel = koinViewModel()
    val exporter: FileExporter = koinInject()
    val importer: FileImporter = koinInject()

    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var localError by remember { mutableStateOf<String?>(null) }
    var pendingImportContent by remember { mutableStateOf<String?>(null) }
    val exportState by viewModel.exportJsonContent.collectAsState()

    // EXPORT (Save) Logic using native FileDialog
    LaunchedEffect(exportState) {
        if (exportState != null) {
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd-HHmm", java.util.Locale.ENGLISH).format(java.util.Date())
            val fileName = "admin-food-backup-$timestamp.json"

            val path = exporter.saveText(fileName, exportState!!)
            if (path != null) {
                viewModel.onExportSaved(success = true)
            } else {
                viewModel.clearPendingExport() // User ne cancel kar diya
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp).statusBarsPadding())

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .clickable { navController.navigateUp() },
                    color = CardBg,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Developer Console",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            if (!state.isAuthenticated) {
                PasswordGate(
                    errorMessage = state.errorMessage,
                    onUnlock = { email, pass -> viewModel.verifyPassword(email, pass) }
                )
            } else {
                ModeControlCard(
                    currentMode = state.currentMode,
                    onModeChange = { viewModel.setMode(it) }
                )

                Spacer(Modifier.height(16.dp))

                PasswordChangeCard(
                    hasCustom = state.hasCustomPassword,
                    onSave = { n, c -> viewModel.changePassword(n, c) }
                )

                Spacer(Modifier.height(16.dp))

                BackupRestoreCard(
                    isExporting = state.isExporting,
                    isImporting = state.isImporting,
                    exportPath = state.exportPath,
                    onExport = { viewModel.prepareExportContent() },
                    onImportClick = {
                        scope.launch {
                            val text = importer.pickJsonFile()
                            if (text != null) {
                                pendingImportContent = text
                            }
                        }
                    },
                    onDismissExportPath = { viewModel.clearExportPath() }
                )

                Spacer(Modifier.height(16.dp))

                InfoCard(currentMode = state.currentMode)

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                ) {
                    Text("Lock Developer Console", fontWeight = FontWeight.Bold)
                }

                state.errorMessage?.let { msg ->
                    Spacer(Modifier.height(12.dp))
                    Banner(message = msg, color = DangerRed, bg = Color(0x33EF4444))
                }
                state.infoMessage?.let { msg ->
                    Spacer(Modifier.height(12.dp))
                    Banner(message = msg, color = AccentGreen, bg = Color(0x3310B981))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    pendingImportContent?.let { content ->
        AlertDialog(
            onDismissRequest = { pendingImportContent = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = CardBg,
            title = {
                Text(
                    "Confirm Import",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "⚠️ WARNING\n\nThis will DELETE all existing data:\n" +
                            "• Users\n• Orders\n• Menu Items\n• Tables\n• Void Logs\n\n" +
                            "and REPLACE it with the backup file.\n\n" +
                            "This cannot be undone. Are you sure?",
                    color = Color.White,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmImport(content)
                        pendingImportContent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Yes, Replace Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingImportContent = null }) {
                    Text("Cancel", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    localError?.let { err ->
        AlertDialog(
            onDismissRequest = { localError = null },
            containerColor = CardBg,
            title = { Text("File Read Error", color = Color.White) },
            text = { Text(err, color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { localError = null }) {
                    Text("OK", color = AccentBlue)
                }
            }
        )
    }
}

@Composable
private fun BackupRestoreCard(
    isExporting: Boolean,
    isImporting: Boolean,
    exportPath: String?,
    onExport: () -> Unit,
    onImportClick: () -> Unit,
    onDismissExportPath: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AccentAmber)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Database Backup & Restore",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Export all data to JSON (for migration to new PC) or restore from a previous backup. Import will OVERWRITE current data.",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onExport,
                enabled = !isExporting && !isImporting,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Exporting...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export Backup (.json)", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onImportClick,
                enabled = !isExporting && !isImporting,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AccentBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
            ) {
                if (isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AccentBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Importing...", fontWeight = FontWeight.Bold, color = AccentBlue)
                } else {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Import Backup (.json)", fontWeight = FontWeight.Bold)
                }
            }

            exportPath?.let { path ->
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = Color(0x3310B981),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Saved to:",
                                color = AccentGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onDismissExportPath) {
                                Text("OK", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            path,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordGate(errorMessage: String?, onUnlock: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... Lock Icon ...
            Spacer(Modifier.height(16.dp))
            Text("Developer Access", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Enter Firebase Credentials to unlock", color = TextMuted, fontSize = 12.sp)

            Spacer(Modifier.height(20.dp))

            // EMAIL FIELD
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Developer Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue, unfocusedBorderColor = CardBorder,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(12.dp))

            // PASSWORD FIELD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        Text(if (visible) "Hide" else "Show", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue, unfocusedBorderColor = CardBorder,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                )
            )

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onUnlock(email, password) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Unlock with Firebase", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))
            Text("Make sure you are online and have a Firebase account.", color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ModeControlCard(currentMode: AppMode, onModeChange: (AppMode) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = AccentGreen)
                Spacer(Modifier.width(8.dp))
                Text(
                    "App Mode",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Choose how the app should run. Local mode disables all online features.",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(16.dp))

            ModeTile(
                mode = AppMode.LOCAL_ONLY,
                isSelected = currentMode == AppMode.LOCAL_ONLY,
                accent = AccentGreen,
                onClick = { onModeChange(AppMode.LOCAL_ONLY) }
            )
            Spacer(Modifier.height(10.dp))
            ModeTile(
                mode = AppMode.ONLINE_APP,
                isSelected = currentMode == AppMode.ONLINE_APP,
                accent = AccentBlue,
                onClick = { onModeChange(AppMode.ONLINE_APP) }
            )
        }
    }
}

@Composable
private fun ModeTile(mode: AppMode, isSelected: Boolean, accent: Color, onClick: () -> Unit) {
    val border by animateColorAsState(
        targetValue = if (isSelected) accent else CardBorder,
        label = "modeBorder"
    )
    val bg by animateColorAsState(
        targetValue = if (isSelected) accent.copy(alpha = 0.12f) else Color.Transparent,
        label = "modeBg"
    )

    Surface(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bg,
        border = BorderStroke(1.5.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = accent,
                    unselectedColor = TextMuted
                )
            )
            Spacer(Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode.label,
                    color = if (isSelected) Color.White else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(mode.description, color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun PasswordChangeCard(hasCustom: Boolean, onSave: (String, String) -> Unit) {
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Developer Password",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                if (hasCustom) "Password has been customized"
                else "Default password in use — please change it!",
                color = if (hasCustom) AccentGreen else DangerRed,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("New Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    onSave(newPass, confirm)
                    newPass = ""
                    confirm = ""
                },
                enabled = newPass.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text("Update Password", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoCard(currentMode: AppMode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Current Configuration",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            InfoRow("Mode", currentMode.label)
            InfoRow(
                "Online Features",
                if (currentMode == AppMode.ONLINE_APP) "Enabled" else "Disabled"
            )
            InfoRow(
                "Firebase Sync",
                if (currentMode == AppMode.ONLINE_APP) "Active" else "Offline"
            )
            InfoRow("Local DB", "Active (always on)")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Banner(message: String, color: Color, bg: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            message,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
}