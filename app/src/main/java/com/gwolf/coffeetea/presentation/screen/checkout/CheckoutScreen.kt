package com.gwolf.coffeetea.presentation.screen.checkout

import android.util.Log
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.StepProgressBar
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.connectivityState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.checkoutScreenState
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        CheckoutPage.Delivery,
        CheckoutPage.PersonalInfo,
        CheckoutPage.Payment
    )
    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(
                navController = navController,
                viewModel = viewModel,
                state = state,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )

            val connection by connectivityState()

            val isConnected = connection === ConnectionState.Available
            if (state.error != null || !isConnected) {
                Log.d(LOGGER_TAG, "Error: ${state.error}")
                val style = if (isConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isConnected) R.string.title_error else R.string.title_network
                val desc = if (isConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                CheckoutScreenContent(
                    snackbarHostState = snackbarHostState,
                    state = state,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope,
                    pagerState = pagerState
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    navController: NavController,
    viewModel: CheckoutViewModel,
    state: CheckoutUiState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = 8.dp),
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
                    .clickable {
                        if (pagerState.canScrollBackward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.targetPage.dec())
                            }
                            viewModel.onEvent(CheckoutEvent.SetStepBar(state.currentStepBar.dec()))
                        } else {
                            navController.popBackStack()
                        }
                    },
                imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                contentDescription = null,
                tint = OnSurfaceColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun CheckoutScreenContent(
    snackbarHostState: SnackbarHostState,
    state: CheckoutUiState,
    viewModel: CheckoutViewModel,
    coroutineScope: CoroutineScope,
    pagerState: PagerState
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                state = pagerState,
                userScrollEnabled = false
            ) { position ->
                when (position) {
                    0 -> {
                        DeliveryPage(
                            snackbarHostState = snackbarHostState,
                            state = state,
                            viewModel = viewModel
                        )
                    }

                    1 -> {
                        PersonalInfoPage(
                            state = state,
                            viewModel = viewModel
                        )
                    }

                    2 -> {

                    }
                }
            }
        }

        val btnEnabled = state.selectedDepartment != null
        CustomButton(
            text = R.string.title_continue,
            isEnabled = true //btnEnabled
        ) {
            if (pagerState.canScrollForward) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.targetPage.inc())
                }
                viewModel.onEvent(CheckoutEvent.SetStepBar(state.currentStepBar.inc()))
            }
        }
    }
}
