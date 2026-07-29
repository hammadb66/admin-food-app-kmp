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
import org.example.adminfoodapp.domain.usecase.AddMenuUseCase
import org.example.adminfoodapp.domain.usecase.CancelConfirmOrderUseCase
import org.example.adminfoodapp.domain.usecase.CancelOrderUseCase
import org.example.adminfoodapp.domain.usecase.CurrentUserUseCase
import org.example.adminfoodapp.domain.usecase.DeleteUserUseCase
import org.example.adminfoodapp.domain.usecase.GetCompleteOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetMenuItemsUseCase
import org.example.adminfoodapp.domain.usecase.GetPendingOrderUseCase
import org.example.adminfoodapp.domain.usecase.GetUsersListUseCase
import org.example.adminfoodapp.domain.usecase.LoginUseCase
import org.example.adminfoodapp.domain.usecase.MenuItemDeleteUseCase
import org.example.adminfoodapp.domain.usecase.OrderAcceptedUseCase
import org.example.adminfoodapp.domain.usecase.OrderDispatchUseCase
import org.example.adminfoodapp.domain.usecase.SendOrderNotificationUseCase
import org.example.adminfoodapp.domain.usecase.SessionOutUseCase
import org.example.adminfoodapp.domain.usecase.SignUpUseCase
import org.example.adminfoodapp.domain.usecase.UpdateMenuItemUseCase
import org.example.adminfoodapp.domain.usecase.UpdateUserAccessUseCase
import org.example.adminfoodapp.presentation.edituserscreen.EditUserViewModel
import org.example.adminfoodapp.presentation.splashscreen.SplashViewModel
import org.example.adminfoodapp.presentation.userprofilescreen.UserProfileViewModel
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.example.adminfoodapp.presentation.viewmodel.SharedEditItemViewModel // Import the new ViewModel
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel
import org.example.adminfoodapp.presentation.tablemanagementscreen.TableManagementViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    factoryOf(::CurrentUserUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::SessionOutUseCase)
    factoryOf(::GetCompleteOrderUseCase)
    factoryOf(::GetPendingOrderUseCase)
    factoryOf(::AddMenuUseCase)
    factoryOf(::GetMenuItemsUseCase)
    factoryOf(::MenuItemDeleteUseCase)
    factoryOf(::UpdateMenuItemUseCase)
    factoryOf(::UpdateMenuItemUseCase)
    factoryOf(::OrderAcceptedUseCase)
    factoryOf(::OrderDispatchUseCase)
    factoryOf(::CancelOrderUseCase)
    factoryOf(::CancelConfirmOrderUseCase)
    factoryOf(::SendOrderNotificationUseCase)
    factoryOf(::GetUsersListUseCase)
    factoryOf(::DeleteUserUseCase)
    factoryOf(::UpdateUserAccessUseCase)
    factoryOf(::SignUpUseCase)



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
    viewModelOf(::SharedEditItemViewModel)
    viewModelOf(::SharedOrderItemViewModel)
    viewModelOf(::ShareUserItemViewModel)
    viewModelOf(::EditUserViewModel)
    viewModelOf(::TableManagementViewModel)


}