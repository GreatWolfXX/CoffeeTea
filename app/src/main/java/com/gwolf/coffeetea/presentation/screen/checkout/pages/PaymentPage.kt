package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import com.google.pay.button.PayButton
import com.gwolf.coffeetea.R
import com.gwolf.coffeetea.navigation.Screen
import com.gwolf.coffeetea.presentation.component.OrderProductCard
import com.gwolf.coffeetea.ui.theme.robotoFontFamily
import com.gwolf.coffeetea.util.allowedPaymentMethods
import timber.log.Timber

@Composable
fun PaymentPage(
    navigateToOtherScreen: (Screen) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(initial = PaymentEvent.Idle)

    LaunchedEffect(event) {
        when (event) {
            is PaymentEvent.Idle -> {}
        }
    }

    val googlePayLauncher = rememberLauncherForActivityResult(
        contract = GetPaymentDataResult()
    ) { taskResult ->
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                taskResult.result!!.let {
                    Timber.d("Google Pay result: ${it.toJson()}")
//                        model.setPaymentData(it)
                }
            }

            CommonStatusCodes.CANCELED -> {
                Timber.d("Google Pay canceled")
            }

            CommonStatusCodes.DEVELOPER_ERROR -> {
                Timber.d("Google Pay error: ${state.error}")
            }
            //else -> Handle internal and other unexpected errors
        }
//            if (result.resultCode == Activity.RESULT_OK) {
//                // Успешная оплата
//                val data: Intent? = result.data
//                Log.d("GooglePay", "Оплата прошла успешно: $data")
//            } else {
//                // Ошибка или отмена
//                Timber.d("Ошибка или отмена платежа")
//            }
    }

    LaunchedEffect(state.paymentDataTask) {
        if (state.paymentDataTask != null) {
            Timber.d("Google Pay launch")
            state.paymentDataTask!!.addOnCompleteListener(googlePayLauncher::launch)
        }
    }

    PaymentContent(
        state = state,
        navigateToOtherScreen = navigateToOtherScreen,
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )
}

@Composable
private fun PaymentContent(
    state: PaymentScreenState,
    navigateToOtherScreen: (Screen) -> Unit,
    onIntent: (PaymentIntent) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(0.9f)
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.product_list),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(4.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(state.cartProductsList) { cartProduct ->
                    OrderProductCard(
                        modifier = Modifier.animateItem(),
                        cartItem = cartProduct,
                        onClick = {
                            navigateToOtherScreen(Screen.ProductInfo(productId = cartProduct.product.id))
                        }
                    )
                }
            }
        }
        AnimatedVisibility(state.isGooglePayAvailable) {
            PayButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                onClick = {
                    onIntent(PaymentIntent.RequestPayment)
                },
                allowedPaymentMethods = allowedPaymentMethods.toString(),
                radius = 100.dp
            )
        }
    }
}

@Preview
@Composable
private fun PaymentPagePreview() {
    PaymentContent(
        state = PaymentScreenState(),
        navigateToOtherScreen = {}
    )
}