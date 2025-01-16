package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.util.PNG_FORMAT
import com.gwolf.coffeetea.util.PROFILE_TABLE
import com.gwolf.coffeetea.util.PROFILE_USER_IMAGE
import com.gwolf.coffeetea.util.USERS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth
) : ProfileRepository {
    override suspend fun getProfile(): Flow<ProfileDto?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROFILE_TABLE)
                .select {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeSingleOrNull<ProfileDto>()
        }
        trySend(response)
        awaitClose()
    }

    override suspend fun uploadProfileImage(bucketId: String, byteArray: ByteArray): Flow<String> =
        callbackFlow {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val bucket = storage.from(bucketId)
            val imageProfile = "$id/$PROFILE_USER_IMAGE$id$PNG_FORMAT"
            bucket.upload(imageProfile, data = byteArray) {
                upsert = true
            }
            trySend(imageProfile)
            awaitClose()
        }

    override suspend fun updateProfileImagePath(imagePath: String): Flow<Unit> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        withContext(Dispatchers.IO) {
            postgrest.from(USERS_TABLE).update(
                {
                    set("image_path", imagePath)
                }
            ) {
                filter {
                    eq("user_id", id)
                }
            }
        }
        trySend(Unit)
        awaitClose()
    }
}