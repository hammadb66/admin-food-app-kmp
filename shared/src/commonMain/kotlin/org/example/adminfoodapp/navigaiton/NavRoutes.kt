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
    data object AddItemScreen : NavRoutes

    @Serializable
    data object AllItemScreen : NavRoutes

    @Serializable
    data object OutOfDeliveryScreen : NavRoutes

    @Serializable
    data object PendingOrderScreen : NavRoutes

    @Serializable
    data object AdminProfileScreen : NavRoutes

    @Serializable
    data object CreateUserScreen : NavRoutes


}