package com.gwolf.coffeetea.presentation.screen.checkout.pages

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import com.google.pay.button.PayButton
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.allowedPaymentMethods

@Composable
fun PaymentPage(
    viewModel: PaymentViewModel = hiltViewModel()
) {
    Column {
        val state by viewModel.paymentScreenState

        val googlePayLauncher = rememberLauncherForActivityResult(
            contract = GetPaymentDataResult()
        ) { taskResult ->
            when (taskResult.status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    taskResult.result!!.let {
                        Log.d(LOGGER_TAG, "Google Pay result: ${it.toJson()}")
//                        model.setPaymentData(it)
                    }
                }

                CommonStatusCodes.CANCELED -> {
                    Log.d(LOGGER_TAG, "Google Pay canceled")
                }

                CommonStatusCodes.DEVELOPER_ERROR -> {
                    Log.d(LOGGER_TAG, "Google Pay error: ${state.error}")
                }
                //else -> Handle internal and other unexpected errors
            }
//            if (result.resultCode == Activity.RESULT_OK) {
//                // Успешная оплата
//                val data: Intent? = result.data
//                Log.d("GooglePay", "Оплата прошла успешно: $data")
//            } else {
//                // Ошибка или отмена
//                Log.e(LOGGER_TAG, "Ошибка или отмена платежа")
//            }
        }

        LaunchedEffect(state.paymentDataTask) {
            if (state.paymentDataTask != null) {
                Log.d(LOGGER_TAG, "Google Pay launch")
                state.paymentDataTask!!.addOnCompleteListener(googlePayLauncher::launch)
            }
        }

        AnimatedVisibility(state.isGooglePayAvailable) {
            PayButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // test price
                    viewModel.onEvent(PaymentEvent.RequestPayment("10"))
                },
                allowedPaymentMethods = allowedPaymentMethods.toString(),
                radius = 100.dp
            )
        }
    }
}