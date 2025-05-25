package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProfileDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.PNG_FORMAT
import com.gwolf.coffeetea.util.PROFILES_BUCKET_ID
import com.gwolf.coffeetea.util.PROFILES_TABLE
import com.gwolf.coffeetea.util.PROFILE_USER_IMAGE
import com.gwolf.coffeetea.util.USERS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth
) : ProfileRepository {
    override fun getProfile(): Flow<Profile?> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROFILES_TABLE)
                .select {
                    filter { eq("user_id", id) }
                }
                .decodeSingleOrNull<ProfileDto>()
        }

        val imagePath = if (response?.imagePath.isNullOrEmpty()) {
            ""
        } else {
            storage.from(PROFILES_BUCKET_ID)
                .createSignedUrl(response?.imagePath.orEmpty(), HOURS_EXPIRES_IMAGE_URL.hours)
        }

        emit(response?.toDomain(imagePath))
    }

    override fun uploadProfileImage(byteArray: ByteArray): Flow<String> =
        flow {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val bucket = storage.from(PROFILES_BUCKET_ID)
            val imageProfile = "$id/$PROFILE_USER_IMAGE$id$PNG_FORMAT"
            bucket.upload(imageProfile, data = byteArray) {
                upsert = true
            }
            emit(imageProfile)
        }

    override fun updateProfileImagePath(imagePath: String): Flow<Unit> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()

        withContext(Dispatchers.IO) {
            postgrest.from(USERS_TABLE).update(
                { set("image_path", imagePath) }
            ) { filter { eq("user_id", id) } }
        }
        emit(Unit)
    }

    override fun updateEmail(newEmail: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            auth.updateUser { email = newEmail }
        }
        emit(Unit)
    }

    override fun updatePhone(newPhone: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            auth.updateUser { phone = newPhone }
        }
        emit(Unit)
    }

    override fun updatePassword(newPassword: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            auth.updateUser { password = newPassword }
        }
        emit(Unit)
    }

    override fun updateNameInfo(
        firstName: String,
        lastName: String,
        patronymic: String
    ): Flow<Unit> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()

        withContext(Dispatchers.IO) {
            postgrest.from(USERS_TABLE).update(
                {
                    if (firstName.isNotBlank()) set("first_name", firstName)
                    if (lastName.isNotBlank()) set("last_name", lastName)
                    if (patronymic.isNotBlank()) set("patronymic", patronymic)
                }
            ) {
                filter { eq("user_id", id) }
            }
        }
        emit(Unit)
    }
}