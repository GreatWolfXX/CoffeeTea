package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<ProfileEntity?>
    fun uploadProfileImage(bucketId: String, byteArray: ByteArray): Flow<String>
    fun updateProfileImagePath(imagePath: String): Flow<Unit>
}