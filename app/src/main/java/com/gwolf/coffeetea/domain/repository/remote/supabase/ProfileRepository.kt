package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile?>
    fun uploadProfileImage(byteArray: ByteArray): Flow<String>
    fun updateProfileImagePath(imagePath: String): Flow<Unit>
    fun updateEmail(newEmail: String): Flow<Unit>
    fun updatePhone(newPhone: String): Flow<Unit>
    fun updatePassword(newPassword: String): Flow<Unit>
    fun updateNameInfo(firstName: String, lastName: String, patronymic: String): Flow<Unit>
}