package com.ilkinbayramov.ninjatalk.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun getPlatformBaseUrl(): String

object ApiClient {
    private val BASE_URL = getPlatformBaseUrl()

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
