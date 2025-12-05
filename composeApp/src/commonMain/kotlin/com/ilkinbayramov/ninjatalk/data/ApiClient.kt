package com.ilkinbayramov.ninjatalk.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // Android emulator localhost

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
            )
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
    }

    fun getBaseUrl() = BASE_URL
}
