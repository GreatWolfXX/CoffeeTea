package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.domain.repository.remote.AuthRepository
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<UiResult<UserInfo?>> = callbackFlow {
        authRepository.signUp(email, password)
            .collect { response ->
                try {
                    trySend(UiResult.Success(data = response))
                    close()
                } catch(e: Exception) {
                    trySend(UiResult.Error(exception = e))
                    close()
                }
            }
        awaitClose()
    }
}