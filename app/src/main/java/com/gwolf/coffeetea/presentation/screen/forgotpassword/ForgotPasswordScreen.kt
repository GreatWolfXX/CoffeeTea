package com.gwolf.coffeetea.presentation.screen.forgotpassword

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import com.gwolf.coffeetea.presentation.screen.login.LoginEvent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = LoginEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is ForgotPasswordEvent.Idle -> {}
            is ForgotPasswordEvent.Navigate -> {
                navController.navigate(Screen.Login)
            }
        }
    }

    ForgotPasswordContent(
        context = context,
        state = state,
        navigateBack = {
            navController.navigateUp()
        },
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )

}

@Composable
private fun ForgotPasswordContent(
    context: Context,
    state: ForgotPasswordScreenState,
    navigateBack: () -> Unit = {},
    onIntent: (ForgotPasswordIntent) -> Unit = {}

) {
    Box {
        TopMenu(
            navigateBack = navigateBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .background(BackgroundGradient)
                .padding(horizontal = 16.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(92.dp))
            Icon(
                modifier = Modifier.size(192.dp),
                imageVector = ImageVector.vectorResource(R.drawable.lock_reset_ic),
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.size(18.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.forgot_password),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.password_recovery_desc),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = OutlineColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(24.dp))
            ForgotPasswordForm(
                context = context,
                state = state,
                onIntent = onIntent
            )
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
                text = stringResource(id = R.string.title_password_recovery),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Color.Black
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
                tint = Color.Black
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ForgotPasswordForm(
    context: Context,
    state: ForgotPasswordScreenState,
    onIntent: (ForgotPasswordIntent) -> Unit
) {
    CustomTextInput(
        icon = Icons.Outlined.MailOutline,
        placeholder = R.string.email_address,
        text = state.email,
        onValueChange = { value ->
            onIntent(ForgotPasswordIntent.Input.EnterEmail(value))
        },
        style = CustomTextInputStyle.EMAIL,
        imeAction = ImeAction.Done,
        isError = state.emailError.asString().isNotBlank(),
        errorMessage = state.emailError
    )
    Spacer(modifier = Modifier.size(4.dp))
    val isError = state.forgotPasswordError.asString().isNotBlank()
    AnimatedVisibility(visible = isError) {
        Text(
            modifier = Modifier,
            text = if (isError) state.forgotPasswordError.asString(context) else "",
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = LightRedColor
        )
    }
    Spacer(modifier = Modifier.size(24.dp))
    CustomButton(text = R.string.send_link)
    {
        onIntent(ForgotPasswordIntent.ButtonClick.Submit)
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenPreview() {
    val context = LocalContext.current
    ForgotPasswordContent(
        context = context,
        state = ForgotPasswordScreenState()
    )
}