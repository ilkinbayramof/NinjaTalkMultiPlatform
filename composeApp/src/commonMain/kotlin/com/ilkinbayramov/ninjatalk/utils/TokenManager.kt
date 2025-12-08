package com.ilkinbayramov.ninjatalk.utils

import com.ilkinbayramov.ninjatalk.data.TokenManager as DataTokenManager

object TokenManager {
    private var cachedToken: String? = null
    private var cachedUserId: String? = null

    suspend fun getToken(): String? {
        if (cachedToken == null) {
            cachedToken = DataTokenManager().getToken()
        }
        return cachedToken
    }

    suspend fun saveToken(token: String) {
        cachedToken = token
        DataTokenManager().saveToken(token)
        // Extract user ID from JWT token (simplified - in production use proper JWT parsing)
        cachedUserId = extractUserIdFromToken(token)
    }

    suspend fun clearToken() {
        cachedToken = null
        cachedUserId = null
        DataTokenManager().clearToken()
    }

    fun getUserId(): String? {
        return cachedUserId
    }

    private fun extractUserIdFromToken(token: String): String? {
        return try {
            // JWT format: header.payload.signature
            val parts = token.split(".")
            if (parts.size != 3) return null

            // Decode base64 payload (simplified)
            val payload = parts[1]
            // In production, properly decode and parse JSON
            // For now, we'll set it when user logs in
            null
        } catch (e: Exception) {
            null
        }
    }

    fun setUserId(userId: String) {
        cachedUserId = userId
    }
}
