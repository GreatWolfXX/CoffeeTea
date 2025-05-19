package com.gwolf.coffeetea.presentation.screen.checkout.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import com.google.pay.button.PayButton
import com.gwolf.coffeetea.util.allowedPaymentMethods
import timber.log.Timber

@Composable
fun PaymentPage(
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
        onIntent = { intent ->
            viewModel.onIntent(intent)
        }
    )
}

@Composable
private fun PaymentContent(
    state: PaymentScreenState,
    onIntent: (PaymentIntent) -> Unit = {}
) {
    Column {
        AnimatedVisibility(state.isGooglePayAvailable) {
            PayButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // test price
                    onIntent(PaymentIntent.RequestPayment("10"))
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
        state = PaymentScreenState()
    )
}