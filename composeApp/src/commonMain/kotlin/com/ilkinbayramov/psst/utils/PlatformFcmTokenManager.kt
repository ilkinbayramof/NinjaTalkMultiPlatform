package com.ilkinbayramov.psst.utils

expect object PlatformFcmTokenManager {
    fun getToken(): String?
    suspend fun sendTokenToBackend()
}
