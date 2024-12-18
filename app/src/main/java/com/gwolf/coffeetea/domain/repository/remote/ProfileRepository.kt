package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<UiResult<ProfileDto?>>
    suspend fun uploadProfileImage(bucketId: String, byteArray: ByteArray): Flow<UiResult<String>>
    suspend fun updateProfileImagePath(imagePath: String): Flow<UiResult<Unit>>
}