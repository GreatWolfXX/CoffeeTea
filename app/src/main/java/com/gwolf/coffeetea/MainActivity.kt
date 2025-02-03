package com.gwolf.coffeetea

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.navigation.SetupNavGraph
import com.gwolf.coffeetea.presentation.component.BottomBar
import com.gwolf.coffeetea.ui.theme.CoffeeTeaTheme
import com.gwolf.coffeetea.ui.theme.StatusBarBackgroundColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = StatusBarBackgroundColor.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )



        setContent {
            CoffeeTeaTheme {
                val screenWithoutBottomBar = listOf(
                    Screen.Welcome,
                    Screen.Auth,
                    Screen.Login,
                    Screen.Registration,
                    Screen.ForgotPassword,
                    Screen.Error
                )

                val screen by splashViewModel.startDestination
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val showBottomBar = navBackStackEntry?.destination?.let { destination ->
                    !screenWithoutBottomBar.any { screen ->
                        destination.hasRoute(screen::class)
                    }
                }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = {

                        AnimatedVisibility(
                            visible = showBottomBar ?: false,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeOut()
                        ) {
                            BottomBar(
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    screen?.let {
                        SetupNavGraph(
                            navController = navController,
                            startDestination = it,
                            showBottomBar = showBottomBar ?: false,
                            paddingValues = innerPadding
                        )
                    }
                }
            }
        }
    }
}

fun Activity.updateStatusBarIconColor() {
    val statusBarColor = window.statusBarColor
    val isColorLight = ColorUtils.calculateLuminance(statusBarColor) > 0.5

    if (isColorLight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val windowInsetController = window?.decorView?.let {
                ViewCompat.getWindowInsetsController(it)
            }
            windowInsetController?.isAppearanceLightStatusBars = true
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val windowInsetController = window?.decorView?.let {
                ViewCompat.getWindowInsetsController(it)
            }
            windowInsetController?.isAppearanceLightStatusBars = false
        }
    }
}