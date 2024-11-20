package com.gwolf.coffeetea.domain.usecase.database

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
    operator fun invoke(): Flow<UiResult<Profile?>> = callbackFlow {
        profileRepository.getProfile().collect { result ->
            when (result) {
                is UiResult.Success -> {
                    val data = result.data
                    val imageUrl =
                        if (data?.bucketId.isNullOrEmpty() || data?.imagePath.isNullOrEmpty()) {
                            ""
                        } else {
                            storage.from(data?.bucketId!!).createSignedUrl(data.imagePath!!, HOURS_EXPIRES_IMAGE_URL.hours)
                        }

                    val profile = data?.toDomain(imageUrl)
                    trySend(UiResult.Success(data = profile))
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