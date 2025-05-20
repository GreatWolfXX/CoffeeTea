package com.gwolf.coffeetea.presentation.screen.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.StepProgressBar
import com.gwolf.coffeetea.presentation.screen.checkout.pages.DeliveryPage
import com.gwolf.coffeetea.presentation.screen.checkout.pages.PaymentPage
import com.gwolf.coffeetea.presentation.screen.checkout.pages.PersonalInfoPage
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        CheckoutPages.Delivery,
        CheckoutPages.PersonalInfo,
        CheckoutPages.Payment
    )
    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    val event by viewModel.event.collectAsState(initial = CheckoutEvent.Idle)

    LaunchedEffect(event) {
        when (val currentEvent: CheckoutEvent = event) {
            is CheckoutEvent.Idle -> {}
            is CheckoutEvent.StepBarChanged -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(currentEvent.newPage)
                }
            }
        }
    }

    CheckoutContent(
        state = state,
        isNetworkConnected = isNetworkConnected,
        navigateToOtherScreen = { screen ->
            navController.navigate(screen)
        },
        navigateBack = {
            navController.navigateUp()
        },
        onIntent = { intent ->
            viewModel.onIntent(intent)
        },
        pagerState = pagerState
    )

    LoadingComponent(state.isLoading)
}

@Composable
private fun CheckoutContent(
    state: CheckoutScreenState,
    isNetworkConnected: Boolean,
    navigateToOtherScreen: (Screen) -> Unit,
    navigateBack: () -> Unit = {},
    onIntent: (CheckoutIntent) -> Unit = {},
    pagerState: PagerState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(
                state = state,
                navigateBack = navigateBack,
                onIntent = onIntent,
                canScrollBackward = pagerState.canScrollBackward
            )

            if (state.error.asString().isNotBlank() || !isNetworkConnected) {
                val style =
                    if (isNetworkConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isNetworkConnected) R.string.title_error else R.string.title_network
                val desc = if (isNetworkConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                CheckoutMainSection(
                    state = state,
                    navigateToOtherScreen = navigateToOtherScreen,
                    onIntent = onIntent,
                    pagerState = pagerState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    canScrollBackward: Boolean,
    state: CheckoutScreenState,
    navigateBack: () -> Unit,
    onIntent: (CheckoutIntent) -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_checkout),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = OnSurfaceColor
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        if (canScrollBackward) {
                            onIntent(CheckoutIntent.SetStepBar(state.currentStepBar.dec()))
                        } else {
                            navigateBack()
                        }
                    },
                imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhiteAlpha06
        )
    )
}

@Composable
private fun CheckoutMainSection(
    state: CheckoutScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (CheckoutIntent) -> Unit,
    pagerState: PagerState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(0.8f),
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            StepProgressBar(
                currentStep = state.currentStepBar,
                listTitles = listOf(
                    stringResource(R.string.delivery),
                    stringResource(R.string.recipient),
                    stringResource(R.string.payment),
                )
            )
            Spacer(modifier = Modifier.size(16.dp))
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false,
                verticalAlignment = Alignment.Top
            ) { position ->
                when (position) {
                    0 -> {
                        DeliveryPage {
                            val step = state.currentStepBar.inc()
                            onIntent(CheckoutIntent.SetStepBar(step))
                        }
                    }

                    1 -> {
                        PersonalInfoPage {
                            onIntent(CheckoutIntent.SetStepBar(state.currentStepBar.inc()))
                        }
                    }

                    2 -> {
                        PaymentPage(navigateToOtherScreen)
                    }
                }
            }
        }
    }
}