package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChangePhoneUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(newPhone: String): Flow<UiResult<Unit>> = callbackFlow {
        try {
            profileRepository.updatePhone(newPhone)
                .collect { response ->
                    trySend(UiResult.Success(data = response))
                }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}