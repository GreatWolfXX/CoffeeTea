package com.gwolf.coffeetea.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Screen(
    val isContainsBottomNav: Boolean = false
) {
    @Serializable
    data object Welcome: Screen()
    @Serializable
    data object Auth: Screen()
    @Serializable
    data object Login: Screen()
    @Serializable
    data object Registration: Screen()
    @Serializable
    data object ForgotPassword: Screen()

}

//fun NavBackStackEntry?.fromRoute(): Screen {
//    NavDestination.route?.substringBefore("?")?.substringBefore("/")
//        ?.substringAfterLast(".")?.let {
//            when (it) {
//                KClass.simpleName -> return Screen.Welcome
//                KClass.simpleName -> return Screen.Auth
//                KClass.simpleName -> return Screen.Login
//                KClass.simpleName -> return Screen.Registration
//                KClass.simpleName -> return Screen.ForgotPassword
//                KClass.simpleName -> return Screen.Home
//                KClass.simpleName -> return Screen.Profile
//                KClass.simpleName -> return Screen.Favorite
//                KClass.simpleName -> return Screen.Cart
//                else -> return Screen.Welcome
//            }
//        }
//    return Screen.Welcome
//}
