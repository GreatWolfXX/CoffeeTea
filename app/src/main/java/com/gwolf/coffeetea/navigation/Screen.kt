package com.gwolf.coffeetea.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Screen() {
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

    @Serializable
    data object Home: Screen()
    @Serializable
    data object Profile: Screen()
    @Serializable
    data object Favorite: Screen()
    @Serializable
    data object Cart: Screen()

    @Serializable
    data object Category: Screen()

    @Serializable
    data class ProductInfo(
        val productId: Int
    ): Screen()

    @Serializable
    data class SearchByCategory(
        val categoryId: Int,
        val categoryName: String
    ): Screen()

}
