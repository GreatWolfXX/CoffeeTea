package com.gwolf.coffeetea.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gwolf.coffeetea.presentation.screen.aboutme.AboutMeScreen
import com.gwolf.coffeetea.presentation.screen.auth.AuthScreen
import com.gwolf.coffeetea.presentation.screen.cart.CartScreen
import com.gwolf.coffeetea.presentation.screen.category.CategoryScreen
import com.gwolf.coffeetea.presentation.screen.changeemal.ChangeEmailScreen
import com.gwolf.coffeetea.presentation.screen.favorite.FavoriteScreen
import com.gwolf.coffeetea.presentation.screen.forgotpassword.ForgotPasswordScreen
import com.gwolf.coffeetea.presentation.screen.home.HomeScreen
import com.gwolf.coffeetea.presentation.screen.login.LoginScreen
import com.gwolf.coffeetea.presentation.screen.productinfo.ProductInfoScreen
import com.gwolf.coffeetea.presentation.screen.profile.ProfileScreen
import com.gwolf.coffeetea.presentation.screen.registration.RegistrationScreen
import com.gwolf.coffeetea.presentation.screen.searchbycategory.SearchByCategoryScreen
import com.gwolf.coffeetea.presentation.screen.welcome.WelcomeScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    showBottomBar: Boolean,
    paddingValues: PaddingValues
) {
    val animatePadding by animateDpAsState(targetValue =
        if (showBottomBar) paddingValues.calculateBottomPadding() / 1.17f else 0.dp)

    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        { fadeIn(tween(1000)) }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
        { fadeOut(tween(700)) }
    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(700)) }

    NavHost(
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = animatePadding),
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Welcome>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            WelcomeScreen(
                navController = navController
            )
        }
        composable<Screen.Auth>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            AuthScreen(
                navController = navController
            )
        }
        composable<Screen.Login>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            LoginScreen(
                navController = navController
            )
        }
        composable<Screen.Registration>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            RegistrationScreen(
                navController = navController
            )
        }
        composable<Screen.ForgotPassword>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            ForgotPasswordScreen(
                navController = navController
            )
        }

        composable<Screen.Home>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            HomeScreen(
                navController = navController
            )
        }
        composable<Screen.Cart>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            CartScreen(
                navController = navController
            )
        }
        composable<Screen.Favorite>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            FavoriteScreen(
                navController = navController
            )
        }
        composable<Screen.Profile>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            ProfileScreen(
                navController = navController
            )
        }

        composable<Screen.Category>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            CategoryScreen(
                navController = navController
            )
        }

        composable<Screen.ProductInfo>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            ProductInfoScreen(
                navController = navController
            )
        }

        composable<Screen.SearchByCategory>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            SearchByCategoryScreen(
                navController = navController
            )
        }

        composable<Screen.AboutMe>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            AboutMeScreen(
                navController = navController
            )
        }

        composable<Screen.ChangeEmail>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition
        ) {
            ChangeEmailScreen(
                navController = navController
            )
        }
    }
}

val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED