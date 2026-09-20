package com.tumbuhsehat.app

import android.app.Application
import com.tumbuhsehat.app.di.AppContainer

class TumbuhSehatApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
