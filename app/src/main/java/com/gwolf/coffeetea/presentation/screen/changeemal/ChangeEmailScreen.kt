package com.gwolf.coffeetea.presentation.screen.changeemal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomTextInput
import com.gwolf.coffeetea.presentation.component.CustomTextInputStyle
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.OtpBottomSheet
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(
    navController: NavController,
    viewModel: ChangeEmailViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = ChangeEmailEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is ChangeEmailEvent.Idle -> {}
            is ChangeEmailEvent.Navigate -> {
                navController.popBackStack()
            }
        }
    }

    ChangeEmailContent(
        state = state,
        isNetworkConnected = isNetworkConnected,
        navigateBack = {
            navController.navigateUp()
        },
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )

    LoadingComponent(state.isLoading)
}

@Composable
private fun ChangeEmailContent(
    state: ChangeEmailScreenState,
    isNetworkConnected: Boolean,
    navigateBack: () -> Unit = {},
    onIntent: (ChangeEmailIntent) -> Unit = {}
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
                navigateBack = navigateBack
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
                ChangeEmailForm(
                    state = state,
                    onIntent = onIntent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    navigateBack: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_change_email),
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
                        navigateBack()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeEmailForm(
    state: ChangeEmailScreenState,
    onIntent: (ChangeEmailIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 16.dp),
                    imageVector = Icons.Outlined.MailOutline,
                    contentDescription = null,
                    tint = OnSurfaceColor
                )
                Column {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.current_email),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OutlineColor
                    )
                    Text(
                        modifier = Modifier,
                        text = state.currentEmail,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.new_email_placeholder,
                text = state.email,
                onValueChange = { value ->
                    onIntent(ChangeEmailIntent.Input.EnterEmail(value))
                },
                style = CustomTextInputStyle.EMAIL,
                imeAction = ImeAction.Done,
                isError = state.emailError.asString().isNotBlank(),
                errorMessage = state.emailError
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        CustomButton(
            text = R.string.btn_save
        )
        {
            onIntent(ChangeEmailIntent.ButtonClick.Submit)
        }
        if (state.showOtpModalSheet) {
            OtpBottomSheet(
                sheetState = sheetState,
                title = R.string.title_verify_email,
                desc = R.string.desc_verify_email,
                isError = state.otpError.asString().isNotBlank(),
                errorMessage = state.otpError,
                onClickConfirm = { otpToken ->
                    onIntent(ChangeEmailIntent.ButtonClick.CheckOtp(otpToken))
                },
                onClickResendCode = {},
                onDismiss = {
                    onIntent(ChangeEmailIntent.ButtonClick.OnDismiss)
                }
            )
        }
    }
}

@Preview
@Composable
private fun ChangeEmailScreenPreview() {
    ChangeEmailContent(
        state = ChangeEmailScreenState(),
        isNetworkConnected = true
    )
}