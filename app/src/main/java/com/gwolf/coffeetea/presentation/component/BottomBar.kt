package com.gwolf.coffeetea.presentation.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gwolf.coffeetea.navigation.BottomBarScreen
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor

@Composable
fun BottomBar(
    navController: NavController
) {
    val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    NavigationBar(
        modifier = Modifier
            .height(60.dp + bottomPadding)
            .shadow(4.dp),
        containerColor = Color.White,
        contentColor = Color.White,
    ) {
        val items = listOf(
            BottomBarScreen.Home,
            BottomBarScreen.Cart,
            BottomBarScreen.Favorite,
            BottomBarScreen.Profile
        )

        val entry by navController.currentBackStackEntryAsState()
        val currentDestination = entry?.destination


        items.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.screen::class)
            } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                },
                selected = selected,
                onClick = {
                    if(!selected) {
                        navController.navigate(destination.screen)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = OnSurfaceColor,
                    indicatorColor = PrimaryDarkColor
                )
            )
        }
    }
}