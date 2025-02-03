package com.gwolf.coffeetea.presentation.screen.registration

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.gwolf.coffeetea.presentation.component.TopMenu
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val formState by remember { viewModel.formState }
    LaunchedEffect(formState.sigUpSuccess) {
        if (formState.sigUpSuccess) {
            navController.navigate(Screen.Home) {
                popUpTo(Screen.Auth) { inclusive = true }
            }
        }
    }
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.registration_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        TopMenu(
            title = R.string.title_welcome,
            onClickBack = {
                navController.navigateUp()
            }
        )
        RegistrationContent(
            context = context,
            navController = navController,
            formState = formState,
            setEmailValue = { email ->
                viewModel.onEvent(RegistrationEvent.EmailChanged(email))
            },
            setRepeatPasswordValue = { repeatPassword ->
                viewModel.onEvent(RegistrationEvent.RepeatPasswordChanged(repeatPassword))
            },
            setPasswordValue = { password ->
                viewModel.onEvent(RegistrationEvent.PasswordChanged(password))
            },
            setPasswordVisible = { passwordVisible ->
                viewModel.onEvent(RegistrationEvent.PasswordVisibleChanged(passwordVisible))
            },
            submit = {
                viewModel.onEvent(RegistrationEvent.Submit)
            }
        )
    }
    LoadingComponent(formState.isLoading)
}

@Composable
private fun BoxScope.RegistrationContent(
    context: Context,
    navController: NavController,
    formState: RegistrationUiState,
    setEmailValue: (String) -> Unit,
    setRepeatPasswordValue: (String) -> Unit,
    setPasswordValue: (String) -> Unit,
    setPasswordVisible: (Boolean) -> Unit,
    submit: () -> Unit
) {
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
            text = stringResource(id = R.string.create_account),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.create_account_desc),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = OutlineColor
        )
        Spacer(modifier = Modifier.size(28.dp))
        CustomTextInput(
            icon = Icons.Outlined.MailOutline,
            placeholder = R.string.email_address,
            text = formState.email,
            onValueChange = setEmailValue,
            style = CustomTextInputStyle.EMAIL,
            imeAction = ImeAction.Next,
            isError = formState.emailError != null,
            errorMessage = formState.emailError
        )
        Spacer(modifier = Modifier.size(8.dp))
        CustomTextInput(
            icon = Icons.Outlined.Lock,
            placeholder = R.string.password_placeholder,
            text = formState.password,
            onValueChange = setPasswordValue,
            style = CustomTextInputStyle.PASSWORD,
            imeAction = ImeAction.Next,
            isError = formState.passwordError != null,
            errorMessage = formState.passwordError,
            passwordVisible = formState.passwordVisible,
            onPasswordVisibleChanged = setPasswordVisible
        )
        Spacer(modifier = Modifier.size(8.dp))
        CustomTextInput(
            icon = Icons.Outlined.Lock,
            placeholder = R.string.repeat_password_placeholder,
            text = formState.repeatPassword,
            onValueChange = setRepeatPasswordValue,
            style = CustomTextInputStyle.PASSWORD,
            imeAction = ImeAction.Done,
            isError = formState.repeatPasswordError != null,
            errorMessage = formState.repeatPasswordError,
            passwordVisible = formState.passwordVisible,
            onPasswordVisibleChanged = setPasswordVisible
        )
        Spacer(modifier = Modifier.size(4.dp))
        val isError = formState.sigUpError != null
        AnimatedVisibility(visible = isError) {
            Text(
                modifier = Modifier,
                text = if(isError) formState.sigUpError!!.asString(context) else "",
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = LightRedColor
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        CustomButton(text = R.string.btn_back)
        {
            submit.invoke()
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Screen.Login)
                },
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.already_have),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = OutlineColor
            )
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.login),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}