package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddImageProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(byteArray: ByteArray): Flow<UiResult<String>> =
        callbackFlow {
            try {
                profileRepository.uploadProfileImage(byteArray).collect { response ->
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