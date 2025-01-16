package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Profile
import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<UiResult<Profile>> = callbackFlow {
        try {
            profileRepository.getProfile().collect { response ->
                val imageUrl =
                    storage.from(response.bucketId)
                        .createSignedUrl(response.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                val profile = response.toDomain(imageUrl)
                trySend(UiResult.Success(data = profile))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}