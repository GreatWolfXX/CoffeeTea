package com.gwolf.coffeetea.domain.usecase.auth

import com.google.firebase.auth.FirebaseAuth
import com.gwolf.coffeetea.data.local.PreferencesKey
import com.gwolf.coffeetea.domain.usecase.preference.ReadBooleanPreferenceUseCase
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val readBooleanPreferenceUseCase: ReadBooleanPreferenceUseCase,
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(): Flow<UiResult<Unit>> = callbackFlow {
        readBooleanPreferenceUseCase.invoke(PreferencesKey.rememberUserKey)
            .collect { rememberUser ->
                if (!rememberUser) {
                    firebaseAuth.signOut()
                    trySend(UiResult.Error(exception = Exception("User not logged in!")))
                    close()
                } else {
                    trySend(UiResult.Success(data = Unit))
                    close()
                }
            }
        awaitClose()
    }
}