package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import com.gwolf.coffeetea.util.DataResult
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<DataResult<UserInfo>> = flow {
        try {
            authRepository.signUp(email, password)
                .collect { response ->
                    if (response != null) {
                        emit(DataResult.Success(data = response))
                    } else {
                        emit(DataResult.Error(exception = Exception("Registration failed!")))
                    }
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}