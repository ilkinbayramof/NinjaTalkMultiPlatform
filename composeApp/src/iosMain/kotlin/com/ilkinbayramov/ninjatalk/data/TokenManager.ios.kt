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
    }
}
