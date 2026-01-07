package com.ilkinbayramov.ninjatalk.utils

actual object PlatformFcmTokenManager {
    actual fun getToken(): String? {
        return FcmTokenManager.getToken()
    }
    
    actual suspend fun sendTokenToBackend() {
        val fcmToken = getToken()
        if (fcmToken != null) {
            println("📤 PLATFORM: Sending FCM token to backend...")
            val repository = com.ilkinbayramov.ninjatalk.data.repository.UserRepository()
            repository.updateFcmToken(fcmToken)
        } else {
            println("⚠️ PLATFORM: No FCM token to send")
        }
    }
}
