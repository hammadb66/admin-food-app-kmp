package org.example.adminfoodapp

import android.app.Application
import org.example.adminfoodapp.di.appModules
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AdminFoodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AdminFoodApp)
            modules(appModules)
        }
    }
}