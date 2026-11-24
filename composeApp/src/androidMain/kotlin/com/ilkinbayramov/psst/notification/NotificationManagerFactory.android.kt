package com.ilkinbayramov.psst.notification

import com.ilkinbayramov.psst.PsstApplication

/** Android implementation of notification manager factory */
actual fun createNotificationManager(): PlatformNotificationManager {
    println("🔧 FACTORY: Creating notification manager")
    val manager = PlatformNotificationManager(PsstApplication.appContext)
    println("✅ FACTORY: Notification manager created successfully")
    return manager
}
