package org.example.adminfoodapp.di

import org.example.adminfoodapp.cloudinaryuploader.CloudinaryUploader
import org.example.adminfoodapp.database.remote.FirebaseAuthenticationDataSource
import org.example.adminfoodapp.database.remote.FirebaseMenuDataSource
import org.example.adminfoodapp.database.remote.OneSignalApi
import org.example.adminfoodapp.database.repository.AuthRepositoryImpl
import org.example.adminfoodapp.database.repository.MenuRepositoryImpl
import org.example.adminfoodapp.database.repository.NotificationRepositoryImpl
import org.example.adminfoodapp.database.repository.RestaurantRepository
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

    single { RestaurantRepository(get(),get()) }
}