package com.ilkinbayramov.psst.data.repository

import com.ilkinbayramov.psst.data.ApiClient
import com.ilkinbayramov.psst.data.dto.AuthResponse
import com.ilkinbayramov.psst.data.dto.ErrorResponse
import com.ilkinbayramov.psst.data.dto.ForgotPasswordRequest
import com.ilkinbayramov.psst.data.dto.LoginRequest
import com.ilkinbayramov.psst.data.dto.RegisterRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun register(
            email: String,
            password: String,
            username: String,
            gender: String,
            birthDate: String
    ): Result<AuthResponse> {
        return try {
            val response =
                    client.post("$baseUrl/api/auth/register") {
                        contentType(ContentType.Application.Json)
                        setBody(
                                RegisterRequest(
                                        email = email,
                                        password = password,
                                        username = username,
                                        gender = gender,
                                        birthDate = birthDate
                                )
                        )
                    }

            // Read the server's own message instead of letting the body parse blow up, so
            // "Username already taken" actually reaches the user
            if (!response.status.isSuccess()) {
                val error: ErrorResponse = response.body()
                return Result.failure(Exception(error.message))
            }

            Result.success(response.body<AuthResponse>())
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

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response =
                    client
                            .post("$baseUrl/api/auth/forgot-password") {
                                contentType(ContentType.Application.Json)
                                setBody(ForgotPasswordRequest(email = email))
                            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Bir hata oluştu"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
