package org.example.adminfoodapp.utils

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {
    companion object {
        private const val KEY_CURRENT_USER_ID = "CURRENT_USER_KEY"
        private const val KEY_CURRENT_USER_NAME = "CURRENT_USER_NAME"
    }

    // Login hotay waqt User ID aur Name dono save honge
    fun saveSession(userKey: String, userName: String) {
        settings.putString(KEY_CURRENT_USER_ID, userKey)
        settings.putString(KEY_CURRENT_USER_NAME, userName)
    }

    // Current Active User Ki Key / ID
    fun getCurrentUserKey(): String? = settings.getStringOrNull(KEY_CURRENT_USER_ID)

    // Current Active Operator Ka Naam (Bill / Void Security Audit ke liye)
    fun getCurrentUserName(): String = settings.getString(KEY_CURRENT_USER_NAME, "Admin")

    // Logout par session saaf karein
    fun clearSession() {
        settings.remove(KEY_CURRENT_USER_ID)
        settings.remove(KEY_CURRENT_USER_NAME)
    }
}