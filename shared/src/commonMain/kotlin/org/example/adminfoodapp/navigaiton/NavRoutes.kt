package org.example.adminfoodapp.navigaiton

import kotlinx.serialization.Serializable

sealed interface NavRoutes {
    @Serializable
     object SplashScreen : NavRoutes

    @Serializable
     object LoginScreen : NavRoutes

    @Serializable
     object MainScreen : NavRoutes

    @Serializable
     object AddItemScreen : NavRoutes

    @Serializable
     object AllItemScreen : NavRoutes

    @Serializable
     object OutOfDeliveryScreen : NavRoutes

    @Serializable
     object PendingOrderScreen : NavRoutes

    @Serializable
     object AdminProfileScreen : NavRoutes

    @Serializable
     object CreateUserScreen : NavRoutes

    @Serializable
    object EditItemScreen : NavRoutes

    @Serializable
    object OrderDetailsScreen : NavRoutes
    @Serializable
    object EditUserScreen : NavRoutes

    @Serializable
    object UserProfileScreen : NavRoutes

    @Serializable
    object TableManagementScreen : NavRoutes

}