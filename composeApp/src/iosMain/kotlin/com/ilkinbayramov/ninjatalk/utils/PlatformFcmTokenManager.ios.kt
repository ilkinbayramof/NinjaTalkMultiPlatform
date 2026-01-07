package com.ilkinbayramov.ninjatalk.utils

import com.ilkinbayramov.ninjatalk.data.repository.UserRepository
import platform.Foundation.NSUserDefaults

actual object PlatformFcmTokenManager {
    actual fun getToken(): String? {
        val token = NSUserDefaults.standardUserDefaults.stringForKey("fcm_token")
        println("📱 iOS: Getting FCM token - ${if (token != null) "Found" else "Not found"}")
        return token
    }
    
    actual suspend fun sendTokenToBackend() {
        val token = getToken()
        if (token != null) {
            println("📤 iOS: Sending FCM token to backend")
            try {
                UserRepository().updateFcmToken(token)
                println("✅ iOS: FCM token sent successfully")
            } catch (e: Exception) {
                println("❌ iOS: Failed to send FCM token - ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("⚠️ iOS: No FCM token available to send")
        }
    }
}
