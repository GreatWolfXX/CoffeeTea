package com.gwolf.coffeetea

import android.graphics.Color
import android.os.Bundle
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.navigation.SetupNavGraph
import com.gwolf.coffeetea.presentation.component.BottomBar
import com.gwolf.coffeetea.ui.theme.CoffeeTeaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            CoffeeTeaTheme {

                val screenWithoutBottomBar = listOf(
                    Screen.Welcome,
                    Screen.Auth,
                    Screen.Login,
                    Screen.Registration,
                    Screen.ForgotPassword
                )

                val snackbarHostState = remember { SnackbarHostState() }
                val screen by splashViewModel.state.collectAsState()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val showBottomBar = navBackStackEntry?.destination?.let { destination ->
                    !screenWithoutBottomBar.any { screen ->
                        destination.hasRoute(screen::class)
                    }
                }

                CompositionLocalProvider(
                    values = arrayOf(
                        LocalSnackbarHostState provides snackbarHostState
                    )
                ) {
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
                        },
                        snackbarHost = {
                            SnackbarHost(snackbarHostState)
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
}