package com.ilkinbayramov.psst.notification

/** iOS implementation of notification manager factory */
actual fun createNotificationManager(): PlatformNotificationManager {
    return PlatformNotificationManager()
}
