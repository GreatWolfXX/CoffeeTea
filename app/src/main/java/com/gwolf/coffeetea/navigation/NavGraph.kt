package com.gwolf.coffeetea.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gwolf.coffeetea.presentation.screen.auth.AuthScreen
import com.gwolf.coffeetea.presentation.screen.cart.CartScreen
import com.gwolf.coffeetea.presentation.screen.category.CategoryScreen
import com.gwolf.coffeetea.presentation.screen.favorite.FavoriteScreen
import com.gwolf.coffeetea.presentation.screen.forgotpassword.ForgotPasswordScreen
import com.gwolf.coffeetea.presentation.screen.home.HomeScreen
import com.gwolf.coffeetea.presentation.screen.login.LoginScreen
import com.gwolf.coffeetea.presentation.screen.productinfo.ProductInfoScreen
import com.gwolf.coffeetea.presentation.screen.profile.ProfileScreen
import com.gwolf.coffeetea.presentation.screen.registration.RegistrationScreen
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

    NavHost(
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = animatePadding),
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Welcome>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            WelcomeScreen(
                navController = navController
            )
        }
        composable<Screen.Auth>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            AuthScreen(
                navController = navController
            )
        }
        composable<Screen.Login>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            LoginScreen(
                navController = navController
            )
        }
        composable<Screen.Registration>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            RegistrationScreen(
                navController = navController
            )
        }
        composable<Screen.ForgotPassword>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            ForgotPasswordScreen(
                navController = navController
            )
        }

        composable<Screen.Home>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            HomeScreen(
                navController = navController
            )
        }
        composable<Screen.Cart>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            CartScreen(

            )
        }
        composable<Screen.Favorite>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            FavoriteScreen(
                navController = navController
            )
        }
        composable<Screen.Profile>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            ProfileScreen(
                navController = navController
            )
        }

        composable<Screen.Category>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            CategoryScreen(
                navController = navController
            )
        }

        composable<Screen.ProductInfo>(
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable fadeOut(tween(700))
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            ProductInfoScreen(
                navController = navController
            )
        }
    }
}
val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED