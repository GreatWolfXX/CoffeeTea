package com.gwolf.coffeetea.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Welcome : Screen()

    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Registration : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Favorite : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data class ProductInfo(
        val productId: String
    ) : Screen()

    @Serializable
    data class SearchByCategory(
        val categoryId: String,
        val categoryName: String
    ) : Screen()

    @Serializable
    data object Category : Screen()

    @Serializable
    data object AboutMe : Screen()

    @Serializable
    data object MyOrders : Screen()

    @Serializable
    data object Notifications : Screen()

    @Serializable
    data class ChangeEmail(
        val email: String
    ) : Screen()

    @Serializable
    data object ChangePassword : Screen()

    @Serializable
    data class ChangePhone(
        val phone: String
    ) : Screen()

    @Serializable
    data object SavedAddresses : Screen()

    @Serializable
    data class AddAddress(
        val isDefault: Boolean
    ) : Screen()

    @Serializable
    data object Checkout : Screen()
}
