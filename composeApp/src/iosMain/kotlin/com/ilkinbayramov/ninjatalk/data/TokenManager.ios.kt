package com.ilkinbayramov.ninjatalk.data

import platform.Foundation.NSUserDefaults

actual class TokenManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual suspend fun saveToken(token: String) {
        userDefaults.setObject(token, forKey = "auth_token")
    }

    actual suspend fun getToken(): String? {
        return userDefaults.stringForKey("auth_token")
    }

    actual suspend fun clearToken() {
        userDefaults.removeObjectForKey("auth_token")
        userDefaults.removeObjectForKey("user_id") // Also clear userId when clearing token
    }

    actual suspend fun saveUserId(userId: String) {
        userDefaults.setObject(userId, forKey = "user_id")
    }

    actual suspend fun getUserId(): String? {
        return userDefaults.stringForKey("user_id")
    }

    actual suspend fun clearUserId() {
        userDefaults.removeObjectForKey("user_id")
    }

    actual suspend fun saveNotificationsEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, forKey = "notifications_enabled")
    }

    actual suspend fun getNotificationsEnabled(): Boolean {
        // boolForKey returns false if key doesn't exist, we want true as default
        if (userDefaults.objectForKey("notifications_enabled") == null) {
            return true
        }
        return userDefaults.boolForKey("notifications_enabled")
    }

    actual suspend fun saveLanguage(languageCode: String) {
        userDefaults.setObject(languageCode, forKey = "app_language")
    }

    actual suspend fun getLanguage(): String? {
        return userDefaults.stringForKey("app_language")
    }
}
