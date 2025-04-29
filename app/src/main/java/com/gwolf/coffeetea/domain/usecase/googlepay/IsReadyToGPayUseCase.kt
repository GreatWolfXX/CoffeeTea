package com.gwolf.coffeetea.domain.usecase.googlepay

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.isReadyToPayRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class IsReadyToGPayUseCase @Inject constructor(
    private val paymentsClient: PaymentsClient
) {
    operator fun invoke(): Flow<DataResult<Boolean>> = callbackFlow {
        val isReadyToPayRequest = IsReadyToPayRequest.fromJson(isReadyToPayRequest().toString())
        val task = paymentsClient.isReadyToPay(isReadyToPayRequest)
        task.addOnCompleteListener { completedTask ->
            try {
                val result = completedTask.getResult(ApiException::class.java)
                trySend(DataResult.Success(data = result))
            } catch (e: ApiException) {
                trySend(DataResult.Error(exception = e))
            } finally {
                close()
            }
        }
        awaitClose()
    }
}