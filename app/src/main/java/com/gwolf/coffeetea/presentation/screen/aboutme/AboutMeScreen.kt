package com.gwolf.coffeetea.presentation.screen.aboutme

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProfileMenuComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun AboutMeScreen(
    navController: NavController,
    viewModel: AboutMeViewModel = hiltViewModel()
) {
    val state by viewModel.aboutMeScreenState

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
                AboutMeScreenContent(
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
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_about_me),
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
                        navController.popBackStack()
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
private fun AboutMeScreenContent(
    navController: NavController,
    state: AboutMeUiState,
    viewModel: AboutMeViewModel
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
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.title_personal_info),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(24.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.first_name_placeholder,
                text = state.firstName,
                onValueChange = { text ->
                    viewModel.onEvent(AboutMeEvent.FirstNameChanged(text))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.firstNameError != null,
                errorMessage = state.firstNameError
            )
            Spacer(modifier = Modifier.size(8.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.last_name_placeholder,
                text = state.lastName,
                onValueChange = { text ->
                    viewModel.onEvent(AboutMeEvent.LastNameChanged(text))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.lastNameError != null,
                errorMessage = state.lastNameError
            )
            Spacer(modifier = Modifier.size(8.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.patronymic_placeholder,
                text = state.patronymic,
                onValueChange = { text ->
                    viewModel.onEvent(AboutMeEvent.PatronymicChanged(text))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Done,
                isError = state.patronymicError != null,
                errorMessage = state.patronymicError
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier,    
                text = stringResource(id = R.string.title_contact_info),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProfileMenuComponent(
                icon = Icons.Outlined.Email,
                text = state.profile?.email.orEmpty()
            ) {
                navController.navigate(Screen.ChangeEmail(state.profile?.email.orEmpty()))
            }
            Spacer(modifier = Modifier.size(16.dp))
            val phoneEntered = state.profile?.phone.orEmpty().isNotEmpty()
            ProfileMenuComponent(
                icon = Icons.Outlined.Phone,
                text = if(phoneEntered) state.profile?.phone.orEmpty() else stringResource(R.string.add_phone)
            ) {
                navController.navigate(Screen.ChangePhone(state.profile?.phone.orEmpty()))
            }
            Spacer(modifier = Modifier.size(16.dp))
            ProfileMenuComponent(
                icon = Icons.Outlined.Lock,
                text = stringResource(R.string.title_password_change)
            ) {
                navController.navigate(Screen.ChangePassword)
            }
        }
        CustomButton(
            text = R.string.btn_save_change
        )
        {
            viewModel.onEvent(AboutMeEvent.Save)
        }
    }
}