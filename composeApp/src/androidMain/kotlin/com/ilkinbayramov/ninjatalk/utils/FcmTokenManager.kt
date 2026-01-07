package com.ilkinbayramov.ninjatalk.utils

import android.content.Context
import android.content.SharedPreferences

object FcmTokenManager {
    private const val PREF_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"
    
    private var prefs: SharedPreferences? = null
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_FCM_TOKEN, token)?.apply()
        println("💾 FCM: Token saved locally")
    }
    
    fun getToken(): String? {
        return prefs?.getString(KEY_FCM_TOKEN, null)
    }
    
    fun clearToken() {
        prefs?.edit()?.remove(KEY_FCM_TOKEN)?.apply()
    }
}
