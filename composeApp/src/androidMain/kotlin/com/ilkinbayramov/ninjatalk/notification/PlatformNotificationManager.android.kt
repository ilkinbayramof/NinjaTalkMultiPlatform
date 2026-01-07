package com.ilkinbayramov.ninjatalk.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ilkinbayramov.ninjatalk.MainActivity
import com.ilkinbayramov.ninjatalk.R

actual class PlatformNotificationManager(private val context: Context) {

    private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    private val prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)

    companion object {
        private const val CHANNEL_ID = "messages_channel"
        private const val CHANNEL_NAME = "Messages"
        private const val CHANNEL_DESCRIPTION = "Notifications for new messages"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                                    CHANNEL_ID,
                                    CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_DEFAULT
                            )
                            .apply {
                                description = CHANNEL_DESCRIPTION
                            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    actual fun showMessageNotification(
            conversationId: String,
            title: String,
            body: String
    ) {
        println(
                "🔔 NOTIFICATION: Showing notification for conversation=$conversationId, title=$title, body=$body"
        )
        
        // Increment notification count for this conversation
        val countKey = "count_$conversationId"
        val currentCount = prefs.getInt(countKey, 0) + 1
        prefs.edit().putInt(countKey, currentCount).apply()

        // Create intent to open conversation when notification is tapped
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("conversationId", conversationId)
                    putExtra("openConversation", true)
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        conversationId.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        // Build notification
        val notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // App icon
                        .setContentText("$title - $body") // "Yeni Mesaj - SenderName"
                        .apply {
                            // Only show count if > 1
                            if (currentCount > 1) {
                                setContentInfo("$currentCount")
                                setSubText("$currentCount mesaj")
                            }
                        }
                        .setStyle(
                                NotificationCompat.BigTextStyle()
                                        .bigText("$title - $body")
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setNumber(currentCount) // Show notification count badge
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(createDeleteIntent(conversationId)) // Clear count when dismissed
                        .build()

        // Show notification (use conversationId hash as notification ID)
        val notificationId = conversationId.hashCode()
        notificationManager.notify(notificationId, notification)
        println("✅ NOTIFICATION: Successfully notified with ID=$notificationId, count=$currentCount")
    }
    
    private fun createDeleteIntent(conversationId: String): PendingIntent {
        val intent = Intent(context, NotificationDismissReceiver::class.java).apply {
            putExtra("conversationId", conversationId)
        }
        return PendingIntent.getBroadcast(
            context,
            conversationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    actual fun cancelNotification(conversationId: String) {
        // Clear count when notification is cancelled
        prefs.edit().remove("count_$conversationId").apply()
        notificationManager.cancel(conversationId.hashCode())
    }

    actual fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
