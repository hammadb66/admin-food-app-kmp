package org.example.adminfoodapp.di

import org.example.adminfoodapp.cloudinaryuploader.CloudinaryUploader
import org.example.adminfoodapp.data.remote.FirebaseAuthenticationDataSource
import org.example.adminfoodapp.data.remote.FirebaseMenuDataSource
import org.example.adminfoodapp.data.remote.OneSignalApi
import org.example.adminfoodapp.data.repository.AuthRepositoryImpl
import org.example.adminfoodapp.data.repository.MenuRepositoryImpl
import org.example.adminfoodapp.data.repository.NotificationRepositoryImpl
import org.example.adminfoodapp.domain.repository.AuthRepository
import org.example.adminfoodapp.domain.repository.MenuRepository
import org.example.adminfoodapp.domain.repository.NotificationRepository
import org.example.adminfoodapp.utils.SessionManager
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { CloudinaryUploader() }
    single { OneSignalApi() }
    single { SessionManager(get()) }

    single { FirebaseAuthenticationDataSource(get(), get()) }
    single { FirebaseMenuDataSource(get(), get(), get()) }

    single { AuthRepositoryImpl(get()) } bind AuthRepository::class
    single { MenuRepositoryImpl(get()) } bind MenuRepository::class
    single { NotificationRepositoryImpl(get()) } bind NotificationRepository::class
}