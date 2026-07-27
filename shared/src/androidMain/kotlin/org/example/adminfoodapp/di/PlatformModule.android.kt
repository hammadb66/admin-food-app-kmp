package org.example.adminfoodapp.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.utils.NetworkMonitor
import org.koin.dsl.module
import org.koin.core.module.Module

actual val platformModule: Module = module {
    single<Any> { get<Context>() }
    single<Settings> {
        val context = get<Context>()
        val prefs = context.getSharedPreferences("admin_food_app_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }//for share preference
    single { NetworkMonitor(get()) }   //for network monitor
}