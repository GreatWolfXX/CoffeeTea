package com.gwolf.coffeetea.domain.repository.remote

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String, password: String): Flow<Unit>
    fun signUp(email: String, password: String): Flow<UserInfo?>
    fun verifyOtpEmail(email: String, otpToken: String): Flow<Unit>
}