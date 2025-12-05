package com.ilkinbayramov.ninjatalk.data

expect class TokenManager() {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}
