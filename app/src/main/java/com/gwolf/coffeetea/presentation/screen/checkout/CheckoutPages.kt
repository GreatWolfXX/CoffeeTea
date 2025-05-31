package com.gwolf.coffeetea.presentation.screen.checkout

sealed class CheckoutPages {
    data object Delivery : CheckoutPages()

    data object PersonalInfo : CheckoutPages()

    data object Payment : CheckoutPages()

    data object PaymentSuccess : CheckoutPages()
}