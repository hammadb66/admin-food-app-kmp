package org.example.adminfoodapp

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.adminfoodapp.navigaiton.NavRoutes
import org.example.adminfoodapp.presentation.splashscreen.SplashScreen
import org.example.adminfoodapp.presentation.loginscreen.LoginScreen
import org.example.adminfoodapp.utils.ToastManager
import org.koin.compose.koinInject

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val toastManager = koinInject<ToastManager>()   // ✅ Koin se inject, manual remember nahi

    Scaffold(
        snackbarHost = { SnackbarHost(toastManager.snackbarHostState) }
    ) { padding ->
        NavHost(navController = navController, startDestination = NavRoutes.SplashScreen) {
            composable<NavRoutes.SplashScreen> {
                SplashScreen(navController = navController)
            }
            composable<NavRoutes.LoginScreen> {
                LoginScreen(navController = navController)
            }
        }
    }
}