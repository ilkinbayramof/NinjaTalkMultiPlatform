package com.ilkinbayramov.ninjatalk.utils

expect object PlatformFcmTokenManager {
    fun getToken(): String?
    suspend fun sendTokenToBackend()
}
