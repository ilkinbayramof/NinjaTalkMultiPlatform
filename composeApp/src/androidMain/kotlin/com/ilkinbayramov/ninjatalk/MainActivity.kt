package com.ilkinbayramov.ninjatalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Initialize FCM Token Manager
        com.ilkinbayramov.ninjatalk.utils.FcmTokenManager.init(applicationContext)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Get Firebase FCM token and save locally (will be sent after login)
        saveFcmTokenLocally()

        setContent { App() }
    }

    private fun saveFcmTokenLocally() {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                val token = task.result
                println("🔥 FCM: Token obtained")
                com.ilkinbayramov.ninjatalk.utils.FcmTokenManager.saveToken(token)
            } else {
                println("❌ FCM: Failed to get token")
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                )
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
