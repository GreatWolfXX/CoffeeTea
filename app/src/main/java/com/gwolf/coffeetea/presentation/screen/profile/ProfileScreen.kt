package com.gwolf.coffeetea.presentation.screen.profile

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyComponent
import com.gwolf.coffeetea.presentation.component.ErrorOrEmptyStyle
import com.gwolf.coffeetea.presentation.component.LoadingComponent
import com.gwolf.coffeetea.presentation.component.ProfileMenuComponent
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OnSurfaceColor
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.PrimaryDarkColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.ConnectionState
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.connectivityState
import com.gwolf.coffeetea.util.uriToBitmap

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.profileScreenState
    val context = LocalContext.current

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
                ProfileScreenContent(
                    navController = navController,
                    state = state,
                    viewModel = viewModel,
                    context = context
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
        modifier = Modifier.padding(horizontal = 8.dp),
        title = {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.title_profile),
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

@Composable
private fun ProfileScreenContent(
    navController: NavController,
    state: ProfileUiState,
    viewModel: ProfileViewModel,
    context: Context
) {
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onEvent(ProfileEvent.LoadImage(uriToBitmap(context, uri)))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val displayName = "${state.profile?.lastName.orEmpty()} ${state.profile?.firstName.orEmpty()}"

        AccountInfo(
            displayName = displayName,
            email = state.profile?.email.orEmpty(),
            imageUrl = state.profile?.imageUrl.orEmpty(),
            context = context
        ) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        Spacer(modifier = Modifier.size(38.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.AccountCircle,
            text = stringResource(R.string.title_about_me)
        ) {
            navController.navigate(Screen.AboutMe)
        }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.AutoMirrored.Outlined.ListAlt,
            text = stringResource(R.string.title_my_order)
        ) { }
//        Spacer(modifier = Modifier.size(16.dp))
//        ProfileMenuComponent(
//            icon = Icons.Outlined.FavoriteBorder,
//            text = R.string.title_favorites
//        ){ }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.Explore,
            text = stringResource(R.string.title_addresses)
        ) {
            navController.navigate(Screen.SavedAddresses)
        }
//        Spacer(modifier = Modifier.size(16.dp))
//        ProfileMenuComponent(
//            icon = Icons.Outlined.AccountBalanceWallet,
//            text = R.string.title_my_cards
//        ) { }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.Outlined.Notifications,
            text = stringResource(R.string.title_notifications)
        ) { }
        Spacer(modifier = Modifier.size(16.dp))
        ProfileMenuComponent(
            icon = Icons.AutoMirrored.Outlined.Logout,
            text = stringResource(R.string.title_logout),
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
    displayName: String,
    email: String,
    imageUrl: String,
    context: Context,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
    ) {
        val profileMockImg = painterResource(R.drawable.profile_img)
        val profileImg = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()
        Image(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            painter = if (imageUrl.isEmpty()) profileMockImg else rememberAsyncImagePainter(model = profileImg),
            contentScale = ContentScale.Crop,
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
        text = displayName.ifBlank { stringResource(R.string.title_blank_name) },
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = TextUnit(24f, TextUnitType.Sp),
        color = OnSurfaceColor
    )
    Spacer(modifier = Modifier.size(8.dp))
    Text(
        modifier = Modifier,
        text = email.ifEmpty { stringResource(R.string.email_address) },
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = TextUnit(16f, TextUnitType.Sp),
        color = OutlineColor
    )
}