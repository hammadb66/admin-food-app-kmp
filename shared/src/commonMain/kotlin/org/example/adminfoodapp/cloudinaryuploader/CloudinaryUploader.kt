package org.example.adminfoodapp.cloudinaryuploader

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CloudinaryUploader {
    private val client: HttpClient = HttpClient()

    private val cloudName = "dk0rds9qp"
    private val uploadPreset = "pofevoxm"

    suspend fun upload(bytes: ByteArray, publicId: String): Result<String> {
        return try {
            // URL Query Parameters me preset and ID pass karein
            val requestUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload" +
                    "?upload_preset=$uploadPreset&public_id=$publicId"

            val response = client.submitFormWithBinaryData(
                url = requestUrl,
                formData = formData {
                    append(
                        key = "file",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$publicId.jpg\"")
                        }
                    )
                }
            )

            val body = response.bodyAsText()
            println("Cloudinary Response: $body")

            val json = Json.parseToJsonElement(body).jsonObject
            val secureUrl = json["secure_url"]?.jsonPrimitive?.content

            if (secureUrl != null) {
                Result.success(secureUrl)
            } else {
                val errorMessage = json["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content
                    ?: "no secure_url in response"
                Result.failure(Exception("Upload failed: $errorMessage"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}