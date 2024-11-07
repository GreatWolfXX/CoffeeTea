package com.gwolf.coffeetea.presentation.screen.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProfileMenuComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.profileScreenState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        if (state.error != null) {
            Log.d("Coffee&TeaLogger", "Error: ${state.error}")
        } else {
            ProfileScreenContent(
                navController = navController,
                state = state,
                viewModel = viewModel
            )
        }
    }
    LoadingComponent(state.isLoading)
}

@Composable
private fun ProfileScreenContent(
    navController: NavController,
    state: ProfileUiState,
    viewModel: ProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccountInfo(
            name = state.profile?.name.orEmpty(),
            email = state.profile?.email.orEmpty()
        ) {

        }
        Spacer(modifier = Modifier.size(38.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.AccountCircle,
            text = R.string.title_about_me
        ){ }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.AutoMirrored.Outlined.ListAlt,
            text = R.string.title_my_order
        ){ }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.FavoriteBorder,
            text = R.string.title_favorites
        ){ }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.Explore,
            text = R.string.title_address
        ) { }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.AccountBalanceWallet,
            text = R.string.title_my_cards
        ) { }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.Notifications,
            text = R.string.title_notifications
        ) { }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.AutoMirrored.Outlined.Logout,
            text = R.string.title_logout,
            isVisibleArrow = false,
            isVisibleDivider = false
        ) {
            viewModel.onEvent(ProfileEvent.Exit)
            navController.navigate(Screen.Auth)
        }
    }
}

@Composable
private fun AccountInfo(
    name: String?,
    email: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(R.drawable.profile_img),
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .background(PrimaryDarkColor)
                .padding(4.dp)
                .align(Alignment.BottomEnd),
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            tint = Color.White
        )
    }
    Spacer(modifier = Modifier.size(8.dp))
    Text(
        modifier = Modifier,
        text = if(name.isNullOrEmpty()) stringResource(R.string.title_name) else name,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = TextUnit(24f, TextUnitType.Sp),
        color = OnSurfaceColor
    )
    Spacer(modifier = Modifier.size(8.dp))
    Text(
        modifier = Modifier,
        text = if(email.isNullOrEmpty()) stringResource(R.string.email_address) else email,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = TextUnit(16f, TextUnitType.Sp),
        color = OutlineColor
    )
}