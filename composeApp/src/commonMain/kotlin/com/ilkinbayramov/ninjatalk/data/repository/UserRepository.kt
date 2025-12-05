package com.ilkinbayramov.ninjatalk.data.repository

import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.dto.User
import io.ktor.client.call.*
import io.ktor.client.request.*
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
            val response = client.get("$baseUrl/api/users/me") {
                header("Authorization", "Bearer $token")
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
