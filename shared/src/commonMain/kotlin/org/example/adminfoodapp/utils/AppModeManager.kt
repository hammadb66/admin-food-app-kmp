package org.example.adminfoodapp.utils

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppModeManager(private val settings: Settings) {

    companion object {
        private const val KEY_APP_MODE = "DEV_APP_MODE"
        private const val KEY_DEV_PASSWORD = "DEV_PASSWORD"
        private const val KEY_DEV_PASSWORD_SET = "DEV_PASSWORD_SET"
        private const val DEFAULT_DEV_PASSWORD = "dev@2026"
    }

    // 🎯 Reactive mode — UI automatically update hogi jab mode badle
    private val _modeFlow = MutableStateFlow(readModeFromSettings())
    val modeFlow: StateFlow<AppMode> = _modeFlow.asStateFlow()

    private fun readModeFromSettings(): AppMode {
        val stored = settings.getStringOrNull(KEY_APP_MODE)
        return try {
            stored?.let { AppMode.valueOf(it) } ?: AppMode.LOCAL_ONLY
        } catch (_: Exception) {
            AppMode.LOCAL_ONLY
        }
    }

    fun getAppMode(): AppMode = _modeFlow.value

    fun setAppMode(mode: AppMode) {
        settings.putString(KEY_APP_MODE, mode.name)
        _modeFlow.value = mode       // 👈 UI ko turant batao
    }

    fun isOnlineModeEnabled(): Boolean = _modeFlow.value == AppMode.ONLINE_APP

    fun verifyDevPassword(input: String): Boolean {
        val stored = settings.getStringOrNull(KEY_DEV_PASSWORD) ?: DEFAULT_DEV_PASSWORD
        return input == stored
    }

    fun changeDevPassword(newPass: String): Boolean {
        if (newPass.length < 4) return false
        settings.putString(KEY_DEV_PASSWORD, newPass)
        settings.putBoolean(KEY_DEV_PASSWORD_SET, true)
        return true
    }

    fun hasCustomDevPassword(): Boolean =
        settings.getBoolean(KEY_DEV_PASSWORD_SET, false)
}