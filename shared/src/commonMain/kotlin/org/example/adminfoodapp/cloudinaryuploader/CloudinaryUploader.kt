package org.example.adminfoodapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

class CloudinaryUploader {

    private val cloudName = "dk0rds9qp"
    private val uploadPreset = "pofevoxm"
    private val client = HttpClient(CIO)

    suspend fun upload(file: File, publicId: String): Result<String> {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload", formData = formData {
                    append("upload_preset", uploadPreset)
                    append("public_id", publicId)
                    append("file", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                })
            val body = response.bodyAsText()
            val json = Json.parseToJsonElement(body).jsonObject
            val secureUrl = json["secure_url"]?.jsonPrimitive?.content
                ?: return Result.failure(Exception("Upload failed: no secure_url in response"))
            Result.success(secureUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}