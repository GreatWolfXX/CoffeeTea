package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.ProfileDto
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<ProfileDto?>
    suspend fun uploadProfileImage(bucketId: String, byteArray: ByteArray): Flow<String>
    suspend fun updateProfileImagePath(imagePath: String): Flow<Unit>
}