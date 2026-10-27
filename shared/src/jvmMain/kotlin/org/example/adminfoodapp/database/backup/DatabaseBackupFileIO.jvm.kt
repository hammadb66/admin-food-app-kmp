package org.example.adminfoodapp.database.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual suspend fun saveBackupFile(content: String, fileName: String): String? =
    withContext(Dispatchers.IO) {
        try {
            val userHome = System.getProperty("user.home") ?: return@withContext null
            val desktop = File(userHome, "Desktop")
            val targetDir = if (desktop.exists() && desktop.isDirectory) desktop else File(userHome)
            if (!targetDir.exists()) targetDir.mkdirs()
            val file = File(targetDir, fileName)
            file.writeText(content)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }