package com.gwolf.coffeetea.presentation.screen.login

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomTextInput
import com.gwolf.coffeetea.presentation.component.CustomTextInputStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.LinkColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = LoginEvent.Idle)

    LaunchedEffect(event) {
        when(event) {
            is LoginEvent.Idle -> {}
            is LoginEvent.Navigate -> {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Auth) { inclusive = true }
                }
            }
        }
    }
    
    LoginContent(
        context = context,
        state = state,
        navigateToOtherScreen = { screen ->
            navController.navigate(screen)
        },
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
private fun LoginContent(
    context: Context,
    state: LoginScreenState,
    navigateToOtherScreen: (Screen) -> Unit = {},
    navigateBack: () -> Unit = {},
    onIntent: (LoginIntent) -> Unit = {}
) {
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        TopMenu(
            navigateBack = navigateBack
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = BackgroundGradient,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
                .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.login_title),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.login_desc),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = OutlineColor
            )
            Spacer(modifier = Modifier.size(28.dp))
            LoginForm(
                context = context,
                state = state,
                navigateToOtherScreen = navigateToOtherScreen,
                onIntent = onIntent
            )
            Spacer(modifier = Modifier.size(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToOtherScreen(Screen.Registration)
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.dont_have),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = OutlineColor
                )
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.sign_up),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
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
    CenterAlignedTopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.title_welcome),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Color.White
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
                tint = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun LoginForm(
    context: Context,
    state: LoginScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (LoginIntent) -> Unit
) {
    CustomTextInput(
        icon = Icons.Outlined.MailOutline,
        placeholder = R.string.email_address,
        text = state.email,
        onValueChange = { value ->
            onIntent(LoginIntent.Input.EnterEmail(value))
        },
        style = CustomTextInputStyle.EMAIL,
        imeAction = ImeAction.Next,
        isError = state.emailError.asString().isNotBlank(),
        errorMessage = state.emailError
    )
    Spacer(modifier = Modifier.size(4.dp))
    CustomTextInput(
        icon = Icons.Outlined.Lock,
        placeholder = R.string.password_placeholder,
        text = state.password,
        onValueChange = { value ->
            onIntent(LoginIntent.Input.EnterPassword(value))
        },
        style = CustomTextInputStyle.PASSWORD,
        imeAction = ImeAction.Done,
        isError = state.passwordError.asString().isNotBlank(),
        errorMessage = state.passwordError,
        passwordVisible = state.passwordVisible,
        onPasswordVisibleChanged = { value ->
            onIntent(LoginIntent.Input.PasswordVisibleChanged(value))
        }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = state.isRemember,
                onCheckedChange = { value ->
                    onIntent(LoginIntent.Input.IsRememberChanged(value))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryDarkColor,
                    checkedBorderColor = PrimaryDarkColor,
                    uncheckedThumbColor = OutlineColor,
                    uncheckedTrackColor = Color.Transparent,
                    uncheckedBorderColor = OutlineColor
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.remember_me),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = OutlineColor
            )
        }
        Text(
            modifier = Modifier.clickable {
                navigateToOtherScreen(Screen.ForgotPassword)
            },
            text = stringResource(id = R.string.forgot_password),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = LinkColor
        )
    }
    Spacer(modifier = Modifier.size(4.dp))
    val isError = state.signInError.asString().isNotBlank()
    AnimatedVisibility(visible = isError) {
        Text(
            modifier = Modifier,
            text = state.signInError.asString(context),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = LightRedColor
        )
    }
    Spacer(modifier = Modifier.size(16.dp))
    CustomButton(
        text = R.string.login,
        onClick = {
            onIntent(LoginIntent.ButtonClick.Submit)
        }
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    val context = LocalContext.current
    LoginContent(
        context = context,
        state = LoginScreenState()
    )
}