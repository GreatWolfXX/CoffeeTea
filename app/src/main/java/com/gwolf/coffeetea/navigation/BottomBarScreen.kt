package com.gwolf.coffeetea.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val screen: Screen,
    val icon: ImageVector
) {
    data object Home: BottomBarScreen(
        screen = Screen.Home,
        icon = Icons.Outlined.Cottage
    )

    data object Cart: BottomBarScreen(
        screen = Screen.Cart,
        icon = Icons.Outlined.ShoppingCart
    )

    data object Favorite: BottomBarScreen(
        screen = Screen.Favorite,
        icon = Icons.Default.FavoriteBorder
    )

    data object Profile: BottomBarScreen(
        screen = Screen.Profile,
        icon = Icons.Outlined.AccountCircle
    )
}