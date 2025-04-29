package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.PROFILES_BUCKET_ID
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.domain.toDomain
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
    operator fun invoke(): Flow<DataResult<Profile>> = callbackFlow {
        try {
            profileRepository.getProfile().collect { response ->
                if (response != null) {
                    val imageUrl = if(response.imagePath.isEmpty()) "" else storage.from(PROFILES_BUCKET_ID)
                        .createSignedUrl(response.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)

                    val profile = response.toDomain(imageUrl)
                    trySend(DataResult.Success(data = profile))
                } else {
                    trySend(DataResult.Error(exception = Exception("Failed to retrieve user profile!")))
                }
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}