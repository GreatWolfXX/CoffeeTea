package com.gwolf.coffeetea.domain.usecase.auth

import com.google.firebase.auth.FirebaseAuth
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(email: String, password: String): Flow<UiResult<Unit>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(UiResult.Success(data = Unit))
                    close()
                } else {
                    trySend(UiResult.Error(exception = task.exception!!))
                    close()
                }
            }
        awaitClose()
    }
}