package com.lynx.fintech

import android.app.Application
import com.lynx.fintech.di.AppContainer

class LynxApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
