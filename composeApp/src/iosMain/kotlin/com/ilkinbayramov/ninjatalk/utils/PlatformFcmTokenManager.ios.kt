package com.ilkinbayramov.ninjatalk.utils

actual object PlatformFcmTokenManager {
    actual fun getToken(): String? {
        return null // iOS implementation
    }
    
    actual suspend fun sendTokenToBackend() {
        // iOS implementation - not needed yet
    }
}
