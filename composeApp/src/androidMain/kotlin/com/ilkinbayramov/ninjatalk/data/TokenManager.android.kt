package com.ilkinbayramov.ninjatalk.data

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ilkinbayramov.ninjatalk.NinjaTalkApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class TokenManager {
    private val context = NinjaTalkApplication.appContext
    private val masterKey =
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private val sharedPreferences =
            EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

    actual suspend fun saveToken(token: String) =
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putString("auth_token", token).apply()
            }

    actual suspend fun getToken(): String? =
            withContext(Dispatchers.IO) { sharedPreferences.getString("auth_token", null) }

    actual suspend fun clearToken() =
            withContext(Dispatchers.IO) { sharedPreferences.edit().remove("auth_token").apply() }

    actual suspend fun saveNotificationsEnabled(enabled: Boolean) =
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
            }

    actual suspend fun getNotificationsEnabled(): Boolean =
            withContext(Dispatchers.IO) {
                sharedPreferences.getBoolean("notifications_enabled", true) // Default to enabled
            }
}
