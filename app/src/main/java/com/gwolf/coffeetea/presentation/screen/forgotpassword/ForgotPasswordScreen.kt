package com.gwolf.coffeetea.presentation.screen.forgotpassword

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.presentation.component.CustomTextInput
import com.gwolf.coffeetea.presentation.component.CustomTextInputStyle
import com.gwolf.coffeetea.presentation.component.TopMenu
import com.gwolf.coffeetea.ui.theme.BackgroundColor
import com.gwolf.coffeetea.ui.theme.LightRedColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val formState by remember { viewModel.formState }

    LaunchedEffect(formState.forgotPasswordSuccess) {
        if (formState.forgotPasswordSuccess) {
            navController.navigate(Screen.Login)
        }
    }
    Box {
        ForgotPasswordContent(
            context = context,
            formState = formState,
            setEmailValue = { email ->
                viewModel.onEvent(ForgotPasswordEvent.EmailChanged(email))
            },
            submit = {
                viewModel.onEvent(ForgotPasswordEvent.Submit)
            }
        )
        TopMenu(
            title = R.string.title_password_recovery,
            color = Color.Black,
            onClickBack = {
                navController.navigateUp()
            }
        )
    }
}

@Composable
private fun BoxScope.ForgotPasswordContent(
    context: Context,
    formState: ForgotPasswordUiState,
    setEmailValue: (String) -> Unit,
    submit: () -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .align(Alignment.TopCenter)
            .background(BackgroundColor)
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
        CustomTextInput(
            icon = Icons.Outlined.MailOutline,
            placeholder = R.string.email_address,
            text = formState.email,
            onValueChange = setEmailValue,
            style = CustomTextInputStyle.EMAIL,
            imeAction = ImeAction.Done,
            isError = formState.emailError != null,
            errorMessage = formState.emailError
        )
        Spacer(modifier = Modifier.size(4.dp))
        val isError = formState.forgotPasswordError != null
        AnimatedVisibility(visible = isError) {
            Text(
                modifier = Modifier,
                text = if(isError) formState.forgotPasswordError!!.asString(context) else "",
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = LightRedColor
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        CustomButton(text = R.string.send_link)
        {
            submit.invoke()
        }
    }
}