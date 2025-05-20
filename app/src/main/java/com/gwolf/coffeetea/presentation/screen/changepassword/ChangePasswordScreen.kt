package com.gwolf.coffeetea.presentation.screen.changepassword

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
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
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = ChangePasswordEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is ChangePasswordEvent.Idle -> {}
            is ChangePasswordEvent.Navigate -> {
                navController.navigateUp()
            }
        }
    }

    ChangePasswordContent(
        context = context,
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
private fun ChangePasswordContent(
    context: Context,
    state: ChangePasswordScreenState,
    isNetworkConnected: Boolean,
    navigateBack: () -> Unit = {},
    onIntent: (ChangePasswordIntent) -> Unit = {}
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
                ChangePasswordForm(
                    context = context,
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
                text = stringResource(R.string.title_password_change),
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
                    .clickable(onClick = navigateBack),
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
private fun ChangePasswordForm(
    context: Context,
    state: ChangePasswordScreenState,
    onIntent: (ChangePasswordIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Spacer(modifier = Modifier.size(16.dp))
            CustomTextInput(
                context = context,
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.new_password,
                text = state.newPassword,
                onValueChange = { text ->
                    onIntent(ChangePasswordIntent.Input.EnterNewPassword(text))
                },
                style = CustomTextInputStyle.PASSWORD,
                imeAction = ImeAction.Next,
                isError = state.newPasswordError.asString().isNotBlank(),
                errorMessage = state.newPasswordError,
                passwordVisible = state.passwordVisible,
                onPasswordVisibleChanged = { passwordVisible ->
                    onIntent(ChangePasswordIntent.ButtonClick.PasswordVisibleChanged(passwordVisible))
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            CustomTextInput(
                context = context,
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.repeat_new_password,
                text = state.repeatNewPassword,
                onValueChange = { text ->
                    onIntent(ChangePasswordIntent.Input.EnterRepeatNewPassword(text))
                },
                style = CustomTextInputStyle.PASSWORD,
                imeAction = ImeAction.Done,
                isError = state.repeatNewPasswordError.asString().isNotBlank(),
                errorMessage = state.repeatNewPasswordError,
                passwordVisible = state.passwordVisible,
                onPasswordVisibleChanged = { passwordVisible ->
                    onIntent(ChangePasswordIntent.ButtonClick.PasswordVisibleChanged(passwordVisible))
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        CustomButton(
            text = R.string.btn_save
        )
        {
            onIntent(ChangePasswordIntent.ButtonClick.Submit)
        }
    }
}

@Preview
@Composable
private fun ChangePasswordScreenPreview() {
    val context = LocalContext.current

    ChangePasswordContent(
        context = context,
        state = ChangePasswordScreenState(),
        isNetworkConnected = true
    )
}