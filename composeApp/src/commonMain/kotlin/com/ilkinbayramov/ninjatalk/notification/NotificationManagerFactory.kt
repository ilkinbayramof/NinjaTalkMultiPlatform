package com.ilkinbayramov.ninjatalk.notification

/** Create platform-specific notification manager Expect/Actual pattern for dependency injection */
expect fun createNotificationManager(): PlatformNotificationManager
