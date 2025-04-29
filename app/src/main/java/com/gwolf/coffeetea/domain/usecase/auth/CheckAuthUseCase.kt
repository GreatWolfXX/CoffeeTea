package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.util.DataResult
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val readBooleanPreferenceUseCase: ReadBooleanPreferenceUseCase,
    private val auth: Auth
) {
    operator fun invoke(): Flow<DataResult<UserInfo?>> = callbackFlow {
        try {
            auth.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        val userInfo = auth.currentUserOrNull()
                        val rememberUser =
                            readBooleanPreferenceUseCase.invoke(PreferencesKey.rememberUserKey)
                        rememberUser
                            .collect { isRemembered ->
                                if (!isRemembered) {
                                    auth.signOut()
                                    trySend(DataResult.Error(exception = Exception("User not logged in!")))
                                } else {
                                    trySend(DataResult.Success(data = userInfo))
                                }
                            }
                    }
                    is SessionStatus.Initializing -> {}
                    else -> {
                        trySend(DataResult.Error(exception = Exception("User not logged in!")))
                    }
                }
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}