package com.ilkinbayramov.ninjatalk.data.repository

import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.dto.AuthResponse
import com.ilkinbayramov.ninjatalk.data.dto.LoginRequest
import com.ilkinbayramov.ninjatalk.data.dto.RegisterRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun register(
            email: String,
            password: String,
            gender: String,
            birthDate: String
    ): Result<AuthResponse> {
        return try {
            val response =
                    client
                            .post("$baseUrl/api/auth/register") {
                                contentType(ContentType.Application.Json)
                                setBody(
                                        RegisterRequest(
                                                email = email,
                                                password = password,
                                                gender = gender,
                                                birthDate = birthDate
                                        )
                                )
                            }
                            .body<AuthResponse>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response =
                    client
                            .post("$baseUrl/api/auth/login") {
                                contentType(ContentType.Application.Json)
                                setBody(LoginRequest(email = email, password = password))
                            }
                            .body<AuthResponse>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
