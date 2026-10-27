package org.example.adminfoodapp.database.backup

/**
 * Save the JSON content to a platform-appropriate folder.
 * Returns the absolute path where file was saved, or null on failure.
 */
expect suspend fun saveBackupFile(content: String, fileName: String): String?