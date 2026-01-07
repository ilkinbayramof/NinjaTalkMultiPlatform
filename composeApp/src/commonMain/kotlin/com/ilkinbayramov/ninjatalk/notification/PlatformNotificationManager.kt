package com.ilkinbayramov.ninjatalk.notification

/**
 * Platform-agnostic notification manager Expect/Actual pattern for Android and iOS implementations
 */
expect class PlatformNotificationManager {
    /**
     * Show a notification for a new message
     * @param conversationId The conversation ID to open when tapped
     * @param title The notification title (e.g. "Yeni Mesaj")
     * @param body The notification body (e.g. sender name)
     */
    fun showMessageNotification(conversationId: String, title: String, body: String)

    /** Cancel a specific notification by conversation ID */
    fun cancelNotification(conversationId: String)

    /** Cancel all notifications */
    fun cancelAllNotifications()
}
