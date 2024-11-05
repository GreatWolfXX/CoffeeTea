package com.gwolf.coffeetea.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gwolf.coffeetea.presentation.screen.auth.AuthScreen
import com.gwolf.coffeetea.presentation.screen.forgotpassword.ForgotPasswordScreen
import com.gwolf.coffeetea.presentation.screen.login.LoginScreen
import com.gwolf.coffeetea.presentation.screen.registration.RegistrationScreen
import com.gwolf.coffeetea.presentation.screen.welcome.WelcomeScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    paddingValues: PaddingValues
) {
    val animatePadding by animateDpAsState(targetValue =
        if (startDestination.isContainsBottomNav) paddingValues.calculateBottomPadding() / 1.17f else 0.dp)
    NavHost(
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = animatePadding),
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Welcome> {
            WelcomeScreen(
                navController = navController
            )
        }
        composable<Screen.Auth> {
            AuthScreen(
                navController = navController
            )
        }
        composable<Screen.Login> {
            LoginScreen(
                navController = navController
            )
        }
        composable<Screen.Registration> {
            RegistrationScreen(
                navController = navController
            )
        }
        composable<Screen.ForgotPassword> {
            ForgotPasswordScreen(
                navController = navController
            )
        }
    }
}
val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED