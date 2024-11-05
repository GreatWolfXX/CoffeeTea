package com.gwolf.coffeetea.domain.usecase.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(): Flow<Unit> = callbackFlow {
        firebaseAuth.signOut()
        awaitClose()
    }
}