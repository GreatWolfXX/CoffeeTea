package com.gwolf.coffeetea.domain.usecase.googlepay

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.isReadyToPayRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IsReadyToGPayUseCase @Inject constructor(
    private val paymentsClient: PaymentsClient
) {
    operator fun invoke(): Flow<DataResult<Boolean>> = flow {
        try {
            val isReady = IsReadyToPayRequest.fromJson(isReadyToPayRequest().toString())
            val result = paymentsClient.isReadyToPay(isReady).await()
            emit(DataResult.Success(data = result))
        } catch (e: ApiException) {
            emit(DataResult.Error(exception = e))
        }
    }
}