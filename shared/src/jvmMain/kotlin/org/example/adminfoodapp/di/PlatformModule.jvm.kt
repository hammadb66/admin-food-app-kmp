package org.example.adminfoodapp.di

import android.app.Application
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.utils.NetworkMonitor
import org.koin.dsl.module
import org.koin.core.module.Module
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    single<Any> { Application() }
    single<Settings> {
        PreferencesSettings(Preferences.userRoot().node("admin_food_app_prefs"))
    }
    single { NetworkMonitor() }   // ✅ ye line add karo
}