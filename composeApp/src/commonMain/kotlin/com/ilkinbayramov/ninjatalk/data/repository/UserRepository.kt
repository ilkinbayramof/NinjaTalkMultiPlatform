package com.ilkinbayramov.ninjatalk.data.repository

import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.dto.User
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UserRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun getAllUsers(
            minAge: Int? = null,
            maxAge: Int? = null,
            gender: String? = null
    ): Result<List<User>> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            // Build query parameters
            val params = mutableListOf<String>()
            minAge?.let { params.add("minAge=$it") }
            maxAge?.let { params.add("maxAge=$it") }
            gender?.let { params.add("gender=$it") }

            val queryString = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""

            val response =
                    client.get("$baseUrl/api/users$queryString") {
                        header("Authorization", "Bearer $token")
                    }

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

            if (response.status == HttpStatusCode.OK) {
                val responseBody: Map<String, String> = response.body()
                Result.success(responseBody["imageUrl"] ?: "")
            } else {
                Result.failure(Exception("Failed to upload image: ${response.status}"))
            }
        } catch (e: Exception) {
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

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.delete("$baseUrl/api/users/me") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                Result.failure(Exception(errorBody["error"] ?: "Failed to delete account"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun blockUser(userId: String): Result<Unit> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.post("$baseUrl/api/users/block") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("blockedUserId" to userId))
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                Result.failure(Exception(errorBody["error"] ?: "Failed to block user"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun unblockUser(userId: String): Result<Unit> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.delete("$baseUrl/api/users/unblock/$userId") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                Result.failure(Exception(errorBody["error"] ?: "Failed to unblock user"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getBlockedUsers(): Result<List<User>> {
        return try {
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.get("$baseUrl/api/users/blocked") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                val users: List<User> = response.body()
                Result.success(users)
            } else {
                val errorBody: Map<String, String> = response.body()
                Result.failure(Exception(errorBody["error"] ?: "Failed to get blocked users"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateFcmToken(fcmToken: String): Result<Unit> {
        return try {
            val token =
                    TokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            println("📤 REPO: Sending FCM token to backend...")
            println("🔑 REPO: User token: ${token.take(20)}...")

            val response =
                    client.post("$baseUrl/api/users/fcm-token") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("token" to fcmToken))
                    }

            println("📥 REPO: FCM token response status: ${response.status}")

            if (response.status == HttpStatusCode.OK) {
                println("✅ REPO: FCM token updated successfully")
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                println("❌ REPO: FCM token update failed - ${errorBody["error"]}")
                Result.failure(Exception(errorBody["error"] ?: "Failed to update FCM token"))
            }
        } catch (e: Exception) {
            println("❌ REPO: FCM token exception - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
