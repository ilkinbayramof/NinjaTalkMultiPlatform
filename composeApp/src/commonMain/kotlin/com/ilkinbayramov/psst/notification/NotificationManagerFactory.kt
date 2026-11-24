package com.ilkinbayramov.psst.notification

/** Create platform-specific notification manager Expect/Actual pattern for dependency injection */
expect fun createNotificationManager(): PlatformNotificationManager
