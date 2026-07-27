package org.example.adminfoodapp.utils

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {
    companion object {
        private const val KEY_CURRENT_USER = "CURRENT_USER_KEY"
    }

    fun saveCurrentUserKey(key: String) = settings.putString(KEY_CURRENT_USER, key)
    fun getCurrentUserKey(): String? = settings.getStringOrNull(KEY_CURRENT_USER)
    fun clearSession() = settings.remove(KEY_CURRENT_USER)
}

