package com.gwolf.coffeetea.domain.repository.remote

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Flow<Unit>
    suspend fun signUp(email: String, password: String): Flow<UserInfo?>
}