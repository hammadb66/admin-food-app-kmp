package org.example.adminfoodapp.utils

interface FileExporter {
    suspend fun saveText(suggestedName: String, content: String): String?
}

interface FileImporter {
    suspend fun pickJsonFile(): String?
}