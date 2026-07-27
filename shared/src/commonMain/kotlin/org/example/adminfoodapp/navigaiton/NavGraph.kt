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
import org.example.adminfoodapp.presentation.loginscreen.LoginScreen
import org.example.adminfoodapp.presentation.mainScreen.MainScreen
import org.example.adminfoodapp.presentation.outofdeliveryscreen.OutOfDeliveryScreen
import org.example.adminfoodapp.presentation.pendingorderscreen.PendingOrderScreen
import org.example.adminfoodapp.presentation.splashscreen.SplashScreen
import org.example.adminfoodapp.utils.ToastManager
import org.koin.compose.koinInject


@Composable
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraph() {
    val navController = rememberNavController()
    val toastManager = koinInject<ToastManager>()

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

            composable<NavRoutes.AllItemScreen> {
                AllItemScreen(navController = navController)
            }
            composable<NavRoutes.OutOfDeliveryScreen> {
                OutOfDeliveryScreen(navController = navController)
            }
            composable<NavRoutes.PendingOrderScreen> {
                PendingOrderScreen(navController = navController)
            }
            composable<NavRoutes.AdminProfileScreen> {
                AdminProfileScreen(navController = navController)
            }
            composable<NavRoutes.CreateUserScreen> {
                CreateUserScreen(navController = navController)
            }













        }
    }
}