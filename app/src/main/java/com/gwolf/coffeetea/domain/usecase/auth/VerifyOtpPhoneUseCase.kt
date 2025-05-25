package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyOtpPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(phone: String, otpToken: String): Flow<DataResult<Unit>> = flow {
        try {
            authRepository.verifyOtpPhone(phone, otpToken)
                .collect { response ->
                    emit(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}