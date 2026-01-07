package com.ilkinbayramov.ninjatalk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val conversationId = intent.getStringExtra("conversationId") ?: return
        
        // Clear notification count when dismissed
        val prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
        prefs.edit().remove("count_$conversationId").apply()
        
        println("🗑️ NOTIFICATION: Cleared count for conversation=$conversationId")
        
        // Update summary notification
        val notificationManager = PlatformNotificationManager(context)
        notificationManager.cancelNotification(conversationId)
    }
}
