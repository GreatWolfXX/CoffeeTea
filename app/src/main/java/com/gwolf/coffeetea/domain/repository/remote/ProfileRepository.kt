package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<ProfileEntity?>
    suspend fun uploadProfileImage(bucketId: String, byteArray: ByteArray): Flow<String>
    suspend fun updateProfileImagePath(imagePath: String): Flow<Unit>
}