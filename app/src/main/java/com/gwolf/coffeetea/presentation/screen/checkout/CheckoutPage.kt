package com.gwolf.coffeetea.presentation.screen.checkout

sealed class CheckoutPage {
    data object Delivery : CheckoutPage()

    data object PersonalInfo : CheckoutPage()

    data object Payment : CheckoutPage()
}