package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DAYS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.PROFILES_BUCKET_ID
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class UpdateProfileImageUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storage: Storage
) {
    operator fun invoke(imagePath: String): Flow<DataResult<String>> =
        flow {
            try {
                profileRepository.updateProfileImagePath(imagePath).collect {
                    val imageUrl = storage.from(PROFILES_BUCKET_ID)
                        .createSignedUrl(imagePath, DAYS_EXPIRES_IMAGE_URL.days)
                    emit(DataResult.Success(data = imageUrl))
                }
            } catch (e: Exception) {
                emit(DataResult.Error(exception = e))
            }
        }
}