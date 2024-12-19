package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.util.DAYS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class UpdateProfileImageUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storage: Storage
) {
    operator fun invoke(bucketId: String, imagePath: String): Flow<UiResult<String>> = callbackFlow {
        profileRepository.updateProfileImagePath(imagePath).collect { result ->
            when(result) {
                is UiResult.Success -> {
                    val imageUrl = storage.from(bucketId).createSignedUrl(imagePath, DAYS_EXPIRES_IMAGE_URL.days)
                    trySend(UiResult.Success(data = imageUrl))
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