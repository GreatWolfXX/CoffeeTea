package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<ProfileEntity?>
    fun uploadProfileImage(byteArray: ByteArray): Flow<String>
    fun updateProfileImagePath(imagePath: String): Flow<Unit>
    fun updateEmail(newEmail: String): Flow<Unit>
    fun updatePhone(newPhone: String): Flow<Unit>
    fun updatePassword(newPassword: String): Flow<Unit>
    fun updateNameInfo(firstName: String, lastName: String, patronymic: String): Flow<Unit>
}