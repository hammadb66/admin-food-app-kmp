package org.example.adminfoodapp.database.backup

import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual suspend fun saveBackupFile(content: String, fileName: String): String? =
    withContext(Dispatchers.IO) {
        try {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            file.writeText(content)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }