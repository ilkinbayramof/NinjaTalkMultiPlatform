package com.ilkinbayramov.ninjatalk.data

expect class TokenManager() {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun clearUserId()
    suspend fun saveNotificationsEnabled(enabled: Boolean)
    suspend fun getNotificationsEnabled(): Boolean
}
