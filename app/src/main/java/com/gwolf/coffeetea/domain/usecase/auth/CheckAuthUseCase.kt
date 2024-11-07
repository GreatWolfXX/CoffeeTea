package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.util.UiResult
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
    operator fun invoke(): Flow<UiResult<UserInfo?>> = callbackFlow {
        auth.sessionStatus.collect { sessionStatus ->
            when(sessionStatus) {
                is SessionStatus.Authenticated -> {
                    val userInfo = auth.currentUserOrNull()
                    readBooleanPreferenceUseCase.invoke(PreferencesKey.rememberUserKey)
                        .collect { rememberUser ->
                            if (!rememberUser) {
                                auth.signOut()
                                trySend(UiResult.Error(exception = Exception("User not logged in!")))
                                close()
                            } else {
                                trySend(UiResult.Success(data = userInfo))
                                close()
                            }
                        }
                }
                is SessionStatus.Initializing -> {}
                else -> {
                    trySend(UiResult.Error(exception = Exception("User not logged in!")))
                    close()
                }
            }
        }
        awaitClose()
    }
}