package org.example.adminfoodapp.navigaiton

import kotlinx.serialization.Serializable

sealed interface NavRoutes {
    @Serializable
    data object SplashScreen : NavRoutes

    @Serializable
    data object LoginScreen : NavRoutes

    @Serializable
    data object MainScreen : NavRoutes

    @Serializable
    data object AllItemsScreen : NavRoutes

    @Serializable
    data class EditItemScreen(val itemKey: String) : NavRoutes   // simple/primitive args best hote hain
}