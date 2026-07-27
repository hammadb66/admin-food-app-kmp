package org.example.adminfoodapp.di

import org.example.adminfoodapp.presentation.additemscreen.AddItemViewModel
import org.example.adminfoodapp.presentation.adminprofilescreen.AdminViewModel
import org.example.adminfoodapp.presentation.createuserscreen.CreateUserViewModel
import org.example.adminfoodapp.presentation.edititemscreen.EditItemViewModel
import org.example.adminfoodapp.presentation.loginscreen.LoginViewModel
import org.example.adminfoodapp.presentation.mainScreen.MainScreenViewModel
import org.example.adminfoodapp.presentation.allitemscreen.MenuViewModel
import org.example.adminfoodapp.presentation.outofdeliveryscreen.OutOfDeliveryViewModel
import org.example.adminfoodapp.presentation.pendingorderscreen.PendingViewModel
import org.example.adminfoodapp.data.repository.AuthRepositoryImpl
import org.example.adminfoodapp.domain.repository.AuthRepository
import org.example.adminfoodapp.domain.usecase.AddMenuUseCase
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetMenuItemsUseCase
import org.example.adminfoodapp.domain.usecase.GetPendingOrderUseCase
import org.example.adminfoodapp.domain.usecase.LoginUseCase
import org.example.adminfoodapp.domain.usecase.MenuItemDeleteUseCase
import org.example.adminfoodapp.domain.usecase.SessionOutUseCase
import org.example.adminfoodapp.presentation.splashscreen.SplashViewModel
import org.example.adminfoodapp.presentation.userprofilescreen.UserProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    single { AuthRepositoryImpl(get()) } bind AuthRepository::class

    factoryOf(::CurrentUserUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::SessionOutUseCase)
    factoryOf(::GetCompleteOrderUseCase)
    factoryOf(::GetPendingOrderUseCase)
    factoryOf(::AddMenuUseCase)
    factoryOf(::GetMenuItemsUseCase)
    factoryOf(::MenuItemDeleteUseCase)



    viewModelOf(::AddItemViewModel)
    viewModelOf(::AdminViewModel)
    viewModelOf(::CreateUserViewModel)
    viewModelOf(::EditItemViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::MenuViewModel)
    viewModelOf(::OutOfDeliveryViewModel)
    viewModelOf(::PendingViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::UserProfileViewModel)

}