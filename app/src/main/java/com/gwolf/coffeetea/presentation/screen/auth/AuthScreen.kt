package com.gwolf.coffeetea.presentation.screen.auth

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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.ui.theme.BackgroundGradient
import com.gwolf.coffeetea.ui.theme.OutlineColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun AuthScreen(
    navController: NavController
) {
    AuthContent(
        navigateToOtherScreen = { screen ->
            navController.navigate(screen)
        },
        navigateBack = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun AuthContent(
    navigateToOtherScreen: (Screen) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.auth_bg),
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
                text = stringResource(id = R.string.title_welcome),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.auth_desc),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = OutlineColor
            )
            Spacer(modifier = Modifier.size(32.dp))
//        CustomButton(
//            text = R.string.title_continue_google,
//            style = CustomButtonStyle.GOOGLE)
//        {
//
//        }
//        Spacer(modifier = Modifier.size(12.dp))
            CustomButton(
                icon = Icons.Outlined.AccountCircle,
                text = R.string.title_create_account
            ) {
                navigateToOtherScreen(Screen.Registration)
            }
            Spacer(modifier = Modifier.size(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToOtherScreen(Screen.Login)
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
                    .clickable(onClick = navigateBack),
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

@Preview
@Composable
private fun AuthScreenPreview() {
    AuthContent()
}
