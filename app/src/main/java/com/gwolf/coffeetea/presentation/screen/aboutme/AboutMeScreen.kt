package com.gwolf.coffeetea.presentation.screen.aboutme

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProfileMenuComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.WhiteAlpha06
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.connectivityState

@Composable
fun AboutMeScreen(
    navController: NavController,
    viewModel: AboutMeViewModel = hiltViewModel()
) {
    val connection by connectivityState()
    val isNetworkConnected = connection === ConnectionState.Available

    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = AboutMeEvent.Idle)

    LaunchedEffect(event) {
        when(event) {
            is AboutMeEvent.Idle -> {}
        }
    }

    AboutMeContent(
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
        }
    )

    LoadingComponent(state.isLoading)
}

@Composable
private fun AboutMeContent(
    state: AboutMeScreenState,
    isNetworkConnected: Boolean,
    navigateToOtherScreen: (Screen) -> Unit = {},
    navigateBack: () -> Unit = {},
    onIntent: (AboutMeIntent) -> Unit = {}
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
                val style = if (isNetworkConnected) ErrorOrEmptyStyle.ERROR else ErrorOrEmptyStyle.NETWORK
                val title = if (isNetworkConnected) R.string.title_error else R.string.title_network
                val desc = if (isNetworkConnected) R.string.desc_error else R.string.desc_network
                ErrorOrEmptyComponent(
                    style = style,
                    title = title,
                    desc = desc
                )
            } else {
                AboutMeForm(
                    state = state,
                    navigateToOtherScreen = navigateToOtherScreen,
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

@Composable
private fun AboutMeForm(
    state: AboutMeScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (AboutMeIntent) -> Unit
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
                onValueChange = { value ->
                    onIntent(AboutMeIntent.Input.EnterFirstName(value))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.firstNameError.asString().isNotBlank(),
                errorMessage = state.firstNameError
            )
            Spacer(modifier = Modifier.size(8.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.last_name_placeholder,
                text = state.lastName,
                onValueChange = { value ->
                    onIntent(AboutMeIntent.Input.EnterLastName(value))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Next,
                isError = state.lastNameError.asString().isNotBlank(),
                errorMessage = state.lastNameError
            )
            Spacer(modifier = Modifier.size(8.dp))
            CustomTextInput(
                icon = Icons.Outlined.MailOutline,
                placeholder = R.string.patronymic_placeholder,
                text = state.patronymic,
                onValueChange = { value ->
                    onIntent(AboutMeIntent.Input.EnterPatronymic(value))
                },
                style = CustomTextInputStyle.STANDARD,
                imeAction = ImeAction.Done,
                isError = state.patronymicError.asString().isNotBlank(),
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
                navigateToOtherScreen(Screen.ChangeEmail(state.profile?.email.orEmpty()))
            }
            Spacer(modifier = Modifier.size(16.dp))
            val phoneEntered = state.profile?.phone.orEmpty().isNotEmpty()
            ProfileMenuComponent(
                icon = Icons.Outlined.Phone,
                text = if(phoneEntered) state.profile?.phone.orEmpty() else stringResource(R.string.add_phone)
            ) {
                navigateToOtherScreen(Screen.ChangePhone(state.profile?.phone.orEmpty()))
            }
            Spacer(modifier = Modifier.size(16.dp))
            ProfileMenuComponent(
                icon = Icons.Outlined.Lock,
                text = stringResource(R.string.title_password_change)
            ) {
                navigateToOtherScreen(Screen.ChangePassword)
            }
        }
        CustomButton(
            text = R.string.btn_save_change
        )
        {
            onIntent(AboutMeIntent.ButtonClick.Save)
        }
    }
}

@Preview
@Composable
private fun AboutMeScreenPreview() {
    AboutMeContent(
        state = AboutMeScreenState(),
        isNetworkConnected = true
    )
}