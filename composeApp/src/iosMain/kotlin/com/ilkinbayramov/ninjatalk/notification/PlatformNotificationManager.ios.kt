package com.ilkinbayramov.ninjatalk.notification

/** iOS implementation of notification manager TODO: Implement using UNUserNotificationCenter */
actual class PlatformNotificationManager {
    actual fun showMessageNotification(
            conversationId: String,
            title: String,
            body: String
    ) {
        // TODO: Implement iOS notifications using UNUserNotificationCenter
        println("iOS Notification: $title: $body")
    }

    actual fun cancelNotification(conversationId: String) {
        // TODO: Cancel iOS notification
    }

    actual fun cancelAllNotifications() {
        // TODO: Cancel all iOS notifications
    }
}
