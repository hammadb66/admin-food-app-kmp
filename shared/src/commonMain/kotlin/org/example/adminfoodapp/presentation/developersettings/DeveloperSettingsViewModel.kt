package org.example.adminfoodapp.presentation.developersettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.adminfoodapp.database.repository.RestaurantRepository
import org.example.adminfoodapp.utils.AppMode
import org.example.adminfoodapp.utils.AppModeManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
data class DeveloperUiState(
    val isAuthenticated: Boolean = false,
    val currentMode: AppMode = AppMode.LOCAL_ONLY,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val hasCustomPassword: Boolean = false,
    // Backup state
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportPath: String? = null,
    val showImportConfirm: Boolean = false
)

class DeveloperSettingsViewModel(
    private val appModeManager: AppModeManager,
    private val repository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DeveloperUiState(
            currentMode = appModeManager.getAppMode(),
            hasCustomPassword = appModeManager.hasCustomDevPassword()
        )
    )
    val uiState = _uiState.asStateFlow()


    fun verifyPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and Password required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, errorMessage = null) } // loading state use kar rahe hain
            try {
                // Firebase se login try karo
                val authResult = Firebase.auth.signInWithEmailAndPassword(email, password)

                // Yahan aap check kar sakte ho ke sirf specific email hi allowed hai
                // if (email != "developer@yourdomain.com") throw Exception("Not a developer")

                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        isExporting = false,
                        errorMessage = null,
                        currentMode = appModeManager.getAppMode()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = "Firebase Login Failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun setMode(mode: AppMode) {
        appModeManager.setAppMode(mode)
        _uiState.update {
            it.copy(
                currentMode = mode,
                infoMessage = "Mode changed to ${mode.label}",
                errorMessage = null
            )
        }
    }

    fun changePassword(newPass: String, confirmPass: String) {
        when {
            newPass.length < 4 -> _uiState.update {
                it.copy(errorMessage = "Password must be at least 4 characters")
            }
            newPass != confirmPass -> _uiState.update {
                it.copy(errorMessage = "Passwords do not match")
            }
            else -> {
                appModeManager.changeDevPassword(newPass)
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        infoMessage = "Password updated successfully",
                        hasCustomPassword = true
                    )
                }
            }
        }
    }

    // ------------------------------------------------
    // EXPORT
    // ------------------------------------------------
    fun exportDatabase() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isExporting = true, errorMessage = null, infoMessage = null, exportPath = null)
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd-HHmm", Locale.ENGLISH).format(Date())
            val fileName = "admin-food-backup-$timestamp.json"

            val path = repository.saveBackupToFile(fileName)

            _uiState.update {
                if (path != null) {
                    it.copy(
                        isExporting = false,
                        infoMessage = "Backup saved successfully",
                        exportPath = path
                    )
                } else {
                    it.copy(
                        isExporting = false,
                        errorMessage = "Export failed. Check storage permissions."
                    )
                }
            }
        }
    }

    // ------------------------------------------------
    // IMPORT
    // ------------------------------------------------
    fun requestImport() {
        _uiState.update { it.copy(showImportConfirm = true, errorMessage = null, infoMessage = null) }
    }

    fun cancelImport() {
        _uiState.update { it.copy(showImportConfirm = false) }
    }
    // ------------------------------------------------
// EXPORT WITH USER-CHOSEN LOCATION
// ------------------------------------------------
    private val _exportJsonContent = MutableStateFlow<String?>(null)
    val exportJsonContent = _exportJsonContent.asStateFlow()

    fun prepareExportContent() {
        viewModelScope.launch {
            try {
                val json = repository.exportBackupJson()
                _exportJsonContent.value = json
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to prepare export: ${e.message}")
                }
            }
        }
    }

    fun onExportSaved(success: Boolean, errorMsg: String? = null) {
        _exportJsonContent.value = null
        _uiState.update {
            if (success) {
                it.copy(infoMessage = "Backup exported successfully", errorMessage = null)
            } else {
                it.copy(errorMessage = errorMsg ?: "Export failed", infoMessage = null)
            }
        }
    }

    fun clearPendingExport() {
        _exportJsonContent.value = null
    }
    fun confirmImport(jsonContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showImportConfirm = false, isImporting = true) }

            val result = repository.importBackupJson(jsonString = jsonContent)

            _uiState.update { currentState ->  // 👈 'it' ko 'currentState' kar diya
                result.fold(
                    onSuccess = {
                        currentState.copy(   // 👈 currentState use karo
                            isImporting = false,
                            infoMessage = "Import successful. Please restart the app."
                        )
                    },
                    onFailure = { e ->
                        currentState.copy(   // 👈 currentState use karo
                            isImporting = false,
                            errorMessage = "Import failed: ${e.message}"
                        )
                    }
                )
            }
        }
    }
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
    fun clearInfo() = _uiState.update { it.copy(infoMessage = null) }
    fun clearExportPath() = _uiState.update { it.copy(exportPath = null) }
    fun logout() = _uiState.update { it.copy(isAuthenticated = false) }
}