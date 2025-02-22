package com.gwolf.coffeetea.presentation.screen.welcome

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gwolf.coffeetea.R

sealed class    OnBoardingPages(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int = 0,
    @StringRes val description: Int
) {
    data object First : OnBoardingPages(
        image = R.drawable.onboarding_1,
        title = R.string.onboarding_title_1,
        description = R.string.onboarding_desc_1
    )

    data object Second : OnBoardingPages(
        image = R.drawable.onboarding_2,
        title = R.string.onboarding_title_2,
        description = R.string.onboarding_desc_2
    )

    data object Third : OnBoardingPages(
        image = R.drawable.onboarding_3,
        title = R.string.onboarding_title_3,
        description = R.string.onboarding_desc_3
    )

    data object Fourth : OnBoardingPages(
        image = R.drawable.onboarding_4,
        title = R.string.onboarding_title_4,
        description = R.string.onboarding_desc_4
    )
}