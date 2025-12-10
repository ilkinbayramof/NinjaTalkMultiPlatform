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
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            val response =
                    client.get("$baseUrl/api/users") { header("Authorization", "Bearer $token") }

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
            println("DEBUG: blockUser called for userId: $userId")
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            println("DEBUG: Token retrieved, making API call to block user")
            val response =
                    client.post("$baseUrl/api/users/block") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("blockedUserId" to userId))
                    }

            println("DEBUG: Block API response status: ${response.status}")
            if (response.status == HttpStatusCode.OK) {
                println("DEBUG: User blocked successfully")
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                println("ERROR: Block API failed with error: ${errorBody["error"]}")
                Result.failure(Exception(errorBody["error"] ?: "Failed to block user"))
            }
        } catch (e: Exception) {
            println("ERROR: Exception in blockUser: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun unblockUser(userId: String): Result<Unit> {
        return try {
            println("DEBUG: unblockUser called for userId: $userId")
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            println("DEBUG: Token retrieved, making unblock API call")
            val response =
                    client.delete("$baseUrl/api/users/unblock/$userId") {
                        header("Authorization", "Bearer $token")
                    }

            println("DEBUG: Unblock API response status: ${response.status}")
            if (response.status == HttpStatusCode.OK) {
                println("DEBUG: User unblocked successfully")
                Result.success(Unit)
            } else {
                val errorBody: Map<String, String> = response.body()
                println("ERROR: Unblock API failed: ${errorBody["error"]}")
                Result.failure(Exception(errorBody["error"] ?: "Failed to unblock user"))
            }
        } catch (e: Exception) {
            println("ERROR: Exception in unblockUser: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getBlockedUsers(): Result<List<User>> {
        return try {
            println("DEBUG: getBlockedUsers called")
            val token =
                    com.ilkinbayramov.ninjatalk.utils.TokenManager.getToken()
                            ?: return Result.failure(Exception("Not authenticated"))

            println("DEBUG: Token retrieved, fetching blocked users from API")
            val response =
                    client.get("$baseUrl/api/users/blocked") {
                        header("Authorization", "Bearer $token")
                    }

            println("DEBUG: getBlockedUsers API response status: ${response.status}")
            if (response.status == HttpStatusCode.OK) {
                val users: List<User> = response.body()
                println("DEBUG: Received ${users.size} blocked users from API")
                Result.success(users)
            } else {
                val errorBody: Map<String, String> = response.body()
                println("ERROR: getBlockedUsers API failed: ${errorBody["error"]}")
                Result.failure(Exception(errorBody["error"] ?: "Failed to get blocked users"))
            }
        } catch (e: Exception) {
            println("ERROR: Exception in getBlockedUsers: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
