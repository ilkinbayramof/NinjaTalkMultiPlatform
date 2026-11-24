package com.ilkinbayramov.psst

import android.app.Application
import android.content.Context

class PsstApplication : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
