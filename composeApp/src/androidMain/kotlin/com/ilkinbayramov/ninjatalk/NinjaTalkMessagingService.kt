package com.ilkinbayramov.ninjatalk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NinjaTalkMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("🔥 FCM: New token received")
        com.ilkinbayramov.ninjatalk.utils.FcmTokenManager.saveToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println("🔥 FCM: Message received from ${remoteMessage.from}")

        // Extract data
        val conversationId = remoteMessage.data["conversationId"] ?: return
        val type = remoteMessage.data["type"] ?: return

        println("🔥 FCM: Message type=$type, conversationId=$conversationId")

        if (type == "new_message") {
            val title = remoteMessage.notification?.title ?: "Yeni Mesaj"
            val body = remoteMessage.notification?.body ?: ""

            showNotification(conversationId, title, body)
        }
    }

    private fun showNotification(conversationId: String, title: String, body: String) {
        val notificationManager =
                com.ilkinbayramov.ninjatalk.notification.PlatformNotificationManager(
                        applicationContext
                )
        notificationManager.showMessageNotification(conversationId, title, body)
    }
}
