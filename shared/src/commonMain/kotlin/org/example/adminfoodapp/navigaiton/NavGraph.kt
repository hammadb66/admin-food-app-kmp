package org.example.adminfoodapp.navigaiton

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.adminfoodapp.presentation.additemscreen.AddItemScreen
import org.example.adminfoodapp.presentation.adminprofilescreen.AdminProfileScreen
import org.example.adminfoodapp.presentation.allitemscreen.AllItemScreen
import org.example.adminfoodapp.presentation.createuserscreen.CreateUserScreen
import org.example.adminfoodapp.presentation.edititemscreen.EditItemScreen
import org.example.adminfoodapp.presentation.edituserscreen.EditUserScreen
import org.example.adminfoodapp.presentation.loginscreen.LoginScreen
import org.example.adminfoodapp.presentation.mainScreen.MainScreen
import org.example.adminfoodapp.presentation.orderdetailsscreen.OrderDetailsScreen
import org.example.adminfoodapp.presentation.outofdeliveryscreen.OutOfDeliveryScreen
import org.example.adminfoodapp.presentation.pendingorderscreen.PendingOrderScreen
import org.example.adminfoodapp.presentation.splashscreen.SplashScreen
import org.example.adminfoodapp.presentation.tablemanagementscreen.TableManagementScreen
import org.example.adminfoodapp.presentation.userprofilescreen.UserProfileScreen
import org.example.adminfoodapp.presentation.viewmodel.ShareUserItemViewModel
import org.example.adminfoodapp.utils.ToastManager
import org.koin.compose.koinInject
import org.example.adminfoodapp.presentation.viewmodel.SharedEditItemViewModel
import org.example.adminfoodapp.presentation.viewmodel.SharedOrderItemViewModel

@Composable
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraph() {
    val navController = rememberNavController()
    val toastManager = koinInject<ToastManager>()
    val sharedEditItemViewModel = koinInject<SharedEditItemViewModel>()
    val sharedOrderItemViewModel = koinInject<SharedOrderItemViewModel>()
    val shareUserItemViewModel = koinInject<ShareUserItemViewModel>()
    Scaffold(
        snackbarHost = { SnackbarHost(toastManager.snackbarHostState) }) {
        NavHost(navController = navController, startDestination = NavRoutes.SplashScreen) {
            composable<NavRoutes.SplashScreen> {
                SplashScreen(navController = navController)
            }
            composable<NavRoutes.LoginScreen> {
                LoginScreen(navController = navController)
            }
            composable<NavRoutes.MainScreen> {
                MainScreen(navController = navController)
            }
            composable<NavRoutes.AddItemScreen> {
                AddItemScreen(navController = navController)
            }
            composable<NavRoutes.TableManagementScreen> {
                TableManagementScreen(navController = navController)
            }
            composable<NavRoutes.AllItemScreen> {
                AllItemScreen(navController = navController, sharedEditItemViewModel = sharedEditItemViewModel)
            }
            composable<NavRoutes.OutOfDeliveryScreen> {
                OutOfDeliveryScreen(navController = navController)
            }
            composable<NavRoutes.PendingOrderScreen> {
                PendingOrderScreen(navController = navController, sharedOrderItemViewModel = sharedOrderItemViewModel)
            }
            composable<NavRoutes.OrderDetailsScreen> {
                OrderDetailsScreen(navController = navController, sharedOrderItemViewModel = sharedOrderItemViewModel)
            }
            composable<NavRoutes.AdminProfileScreen> {
                AdminProfileScreen(navController = navController, shareUserItemViewModel = shareUserItemViewModel)
            }
            composable<NavRoutes.EditUserScreen> {
                EditUserScreen(navController = navController, shareUserItemViewModel = shareUserItemViewModel)
            }
            composable<NavRoutes.CreateUserScreen> {
                CreateUserScreen(navController = navController)
            }
            composable<NavRoutes.EditItemScreen> {
                EditItemScreen(navController = navController, sharedEditItemViewModel = sharedEditItemViewModel)
            }
            composable<NavRoutes.UserProfileScreen> {
                UserProfileScreen(navController=navController)
            }
        }
    }
}