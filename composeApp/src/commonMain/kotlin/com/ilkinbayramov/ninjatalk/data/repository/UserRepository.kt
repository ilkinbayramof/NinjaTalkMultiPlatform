package com.ilkinbayramov.ninjatalk.data.repository

import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.dto.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

class UserRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = client.get("$baseUrl/api/users")

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(token: String): Result<User> {
        return try {
            val response =
                    client.get("$baseUrl/api/users/me") { header("Authorization", "Bearer $token") }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(token: String, imageBytes: ByteArray): Result<String> {
        return try {
            println("Uploading image, size: ${imageBytes.size} bytes")
            val response =
                    client.post("$baseUrl/api/users/profile-image") {
                        header("Authorization", "Bearer $token")
                        setBody(
                                io.ktor.client.request.forms.MultiPartFormDataContent(
                                        io.ktor.client.request.forms.formData {
                                            append(
                                                    "image",
                                                    imageBytes,
                                                    io.ktor.http.Headers.build {
                                                        append(
                                                                HttpHeaders.ContentType,
                                                                "image/jpeg"
                                                        )
                                                        append(
                                                                HttpHeaders.ContentDisposition,
                                                                "filename=profile.jpg"
                                                        )
                                                    }
                                            )
                                        }
                                )
                        )
                    }

            println("Response status: ${response.status}")
            println("Response body: ${response.bodyAsText()}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody: Map<String, String> = response.body()
                Result.success(responseBody["imageUrl"] ?: "")
            } else {
                Result.failure(Exception("Failed to upload image: ${response.status}"))
            }
        } catch (e: Exception) {
            println("Upload error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.put("$baseUrl/api/users/password") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(
                                mapOf(
                                        "currentPassword" to currentPassword,
                                        "newPassword" to newPassword
                                )
                        )
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                Result.failure(Exception(errorBody["error"] ?: "Failed to change password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
