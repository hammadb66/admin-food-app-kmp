package org.example.adminfoodapp.database.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.example.adminfoodapp.database.model.OneSignalRequest

class OneSignalApi {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun sendNotification(authKey: String, body: OneSignalRequest): HttpResponse {
        return client.post("https://onesignal.com/api/v1/notifications") {
            contentType(ContentType.Application.Json)
            header("Authorization", authKey)
            setBody(body)
        }
    }
}