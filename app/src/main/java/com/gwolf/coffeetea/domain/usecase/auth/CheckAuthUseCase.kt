package com.gwolf.coffeetea.domain.usecase.auth

import com.gwolf.coffeetea.data.repository.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.util.DataResult
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val readBooleanPreferenceUseCase: ReadBooleanPreferenceUseCase,
    private val auth: Auth
) {
    operator fun invoke(): Flow<DataResult<UserInfo>> = flow {
        try {
            auth.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        val userInfo = auth.currentUserOrNull()
                        if (userInfo == null) {
                            emit(DataResult.Error(exception = Exception("User not logged in!")))
                            return@collect
                        }
                        readBooleanPreferenceUseCase.invoke(PreferencesKey.rememberUserKey)
                            .collect { isRemembered ->
                                if (!isRemembered) {
                                    auth.signOut()
                                    emit(DataResult.Error(exception = Exception("User not logged in!")))
                                } else {
                                    emit(DataResult.Success(data = userInfo))
                                }
                            }
                    }

                    else -> {
                        emit(DataResult.Error(exception = Exception("User not logged in!")))
                    }
                }
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}