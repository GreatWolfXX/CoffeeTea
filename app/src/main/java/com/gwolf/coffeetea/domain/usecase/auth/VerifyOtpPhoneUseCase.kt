package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class VerifyOtpPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(phone: String, otpToken: String): Flow<UiResult<Unit>> = callbackFlow {
        try {
            authRepository.verifyOtpPhone(phone, otpToken)
                .collect { response ->
                    trySend(UiResult.Success(data = response))
                }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}