package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {
    override fun signIn(email: String, password: String): Flow<Unit> = flow {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        emit(Unit)
    }

    override fun signUp(email: String, password: String): Flow<UserInfo?> = flow {
        val response = auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        emit(response)
    }

    override fun verifyOtpEmail(email: String, otpToken: String): Flow<Unit> = flow {
        auth.verifyEmailOtp(type = OtpType.Email.EMAIL_CHANGE, email = email, token = otpToken)
        emit(Unit)
    }

    override fun verifyOtpPhone(phone: String, otpToken: String): Flow<Unit> = flow {
        auth.verifyPhoneOtp(type = OtpType.Phone.PHONE_CHANGE, phone = phone, token = otpToken)
        emit(Unit)
    }
}