package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.CustomButton
import com.gwolf.coffeetea.ui.theme.PrimaryColor
import com.gwolf.coffeetea.ui.theme.robotoFontFamily

@Composable
fun PaymentSuccessPage(
    viewModel: PaymentSuccessViewModel = hiltViewModel(),
    navigateToOtherScreen: (Screen) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = PaymentSuccessEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is PaymentSuccessEvent.Idle -> {}
            is PaymentSuccessEvent.Navigate -> {
                navigateToOtherScreen(Screen.Home)
            }
        }
    }

    PaymentSuccessContent(
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )
}

@Composable
private fun PaymentSuccessContent(
    onIntent: (PaymentSuccessIntent) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .scale(2f)
                    .border(
                        width = 2.dp,
                        color = PrimaryColor,
                        shape = CircleShape
                    ),
                imageVector = Icons.Rounded.Check,
                tint = PrimaryColor,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.payment_finish),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            )
        }


        CustomButton(
            text = R.string.finish
        ) {
            onIntent(PaymentSuccessIntent.Submit)
        }
    }
}

@Preview
@Composable
private fun PaymentSuccessPagePreview() {
    PaymentSuccessContent()
}