package com.gwolf.coffeetea.data.remote.repository.supabase

import com.gwolf.coffeetea.domain.repository.remote.supabase.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {
    override fun signIn(email: String, password: String): Flow<Unit> = callbackFlow {
        val response = auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun signUp(email: String, password: String): Flow<UserInfo?> = callbackFlow {
        val response = auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun verifyOtpEmail(email: String, otpToken: String): Flow<Unit> = callbackFlow  {
        val response = auth.verifyEmailOtp(type = OtpType.Email.EMAIL_CHANGE, email = email, token = otpToken)
        trySend(response)
        close()
        awaitClose()
    }

    override fun verifyOtpPhone(phone: String, otpToken: String): Flow<Unit> = callbackFlow  {
        val response = auth.verifyPhoneOtp(type = OtpType.Phone.PHONE_CHANGE, phone = phone, token = otpToken)
        trySend(response)
        close()
        awaitClose()
    }
}