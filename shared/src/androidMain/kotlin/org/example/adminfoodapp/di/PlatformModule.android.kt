package org.example.adminfoodapp.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import org.example.adminfoodapp.utils.FileExporter
import org.example.adminfoodapp.utils.FileImporter
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
    factory { NetworkMonitor(get()) }
    single<FileExporter> { object : FileExporter {
        override suspend fun saveText(suggestedName: String, content: String): String? = null
    } }
    single<FileImporter> { object : FileImporter {
        override suspend fun pickJsonFile(): String? = null
    } }}