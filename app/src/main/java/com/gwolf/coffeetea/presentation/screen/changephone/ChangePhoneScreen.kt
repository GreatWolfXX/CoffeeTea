package com.gwolf.coffeetea.presentation.screen.changephone

import android.util.Log
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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.UKRAINE_PHONE_CODE
import com.gwolf.coffeetea.util.connectivityState
import com.gwolf.coffeetea.util.getFlagEmoji
import java.util.Locale

@Composable
fun ChangePhoneScreen(
    navController: NavController,
    viewModel: ChangePhoneViewModel = hiltViewModel()
) {
    val state by viewModel.changePhoneState

    LaunchedEffect(state.phoneChanged) {
        if (state.phoneChanged) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(
                navController = navController
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
                ChangePhoneScreenContent(
                    navController = navController,
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }
    LoadingComponent(state.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopMenu(
    navController: NavController
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_change_phone),
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
                        navController.popBackStack()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePhoneScreenContent(
    navController: NavController,
    state: ChangePhoneUiState,
    viewModel: ChangePhoneViewModel
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
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = OnSurfaceColor
                )
                Column {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.current_phone),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OutlineColor
                    )
                    val phoneText = state.currentPhone.ifEmpty { stringResource(R.string.empty_phone) }
                    Text(
                        modifier = Modifier,
                        text = phoneText,
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = TextUnit(20f, TextUnitType.Sp),
                        color = OnSurfaceColor
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            val flag = Locale("uk", "UA").getFlagEmoji()
            CustomTextInput(
                prefixText = "$flag $UKRAINE_PHONE_CODE",
                placeholder = R.string.new_phone_placeholder,
                text = state.phone,
                onValueChange = { text ->
                    viewModel.onEvent(ChangePhoneEvent.PhoneChanged(text))
                },
                style = CustomTextInputStyle.PHONE,
                imeAction = ImeAction.Done,
                isError = state.phoneError != null,
                errorMessage = state.phoneError
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        CustomButton(
            text = R.string.btn_save
        )
        {
            viewModel.onEvent(ChangePhoneEvent.Save)
        }
//        if(state.showOtpModalSheet) {
//            OtpBottomSheet(
//                sheetState = sheetState,
//                title = R.string.title_verify_phone,
//                desc = R.string.desc_verify_phone,
//                isError = state.otpError != null,
//                errorMessage = state.otpError,
//                onClickConfirm = { otpToken ->
//                    viewModel.onEvent(ChangePhoneEvent.CheckOtp(otpToken))
//                },
//                onClickResendCode = {},
//                onDismiss = {
//                    viewModel.onEvent(ChangePhoneEvent.OnDismiss)
//                }
//            )
//        }
    }
}