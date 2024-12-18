package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddImageProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(bucketId: String, byteArray: ByteArray): Flow<UiResult<String>> = callbackFlow {
        profileRepository.uploadProfileImage(bucketId, byteArray).collect { result ->
            when (result) {
                is UiResult.Success -> {

                    trySend(UiResult.Success(data = result.data))
                    close()
                }

                is UiResult.Error -> {
                    trySend(result)
                    close()
                }
            }
        }
        awaitClose()
    }
}