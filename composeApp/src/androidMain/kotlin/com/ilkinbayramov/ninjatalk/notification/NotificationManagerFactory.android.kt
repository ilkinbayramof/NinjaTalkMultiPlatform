package com.ilkinbayramov.ninjatalk.notification

import com.ilkinbayramov.ninjatalk.NinjaTalkApplication

/** Android implementation of notification manager factory */
actual fun createNotificationManager(): PlatformNotificationManager {
    println("🔧 FACTORY: Creating notification manager")
    val manager = PlatformNotificationManager(NinjaTalkApplication.appContext)
    println("✅ FACTORY: Notification manager created successfully")
    return manager
}
