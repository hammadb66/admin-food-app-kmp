package org.example.adminfoodapp.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class JvmFileExporter : FileExporter {
    override suspend fun saveText(suggestedName: String, content: String): String? = withContext(Dispatchers.IO) {
        // Native Windows save dialog
        val dialog = FileDialog(null as Frame?, "Save Backup File", FileDialog.SAVE)
        dialog.file = suggestedName
        dialog.isVisible = true

        val dir = dialog.directory
        val file = dialog.file

        if (dir != null && file != null) {
            val f = File(dir, file)
            try {
                f.writeText(content)
                f.absolutePath
            } catch (e: Exception) { null }
        } else null
    }
}

class JvmFileImporter : FileImporter {
    override suspend fun pickJsonFile(): String? = withContext(Dispatchers.IO) {
        // Native Windows open dialog
        val dialog = FileDialog(null as Frame?, "Open Backup File", FileDialog.LOAD)
        dialog.file = "*.json"
        dialog.isVisible = true

        val dir = dialog.directory
        val file = dialog.file

        if (dir != null && file != null) {
            try {
                File(dir, file).readText()
            } catch (e: Exception) { null }
        } else null
    }
}