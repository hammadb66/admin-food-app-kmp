package org.example.adminfoodapp.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.example.adminfoodapp.utils.ToastManager
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single { ToastManager(get()) }
}