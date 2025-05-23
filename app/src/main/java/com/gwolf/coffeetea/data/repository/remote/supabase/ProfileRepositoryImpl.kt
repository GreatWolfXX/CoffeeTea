package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProfileEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.PNG_FORMAT
import com.gwolf.coffeetea.util.PROFILES_BUCKET_ID
import com.gwolf.coffeetea.util.PROFILES_TABLE
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
    override fun getProfile(): Flow<ProfileEntity?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PROFILES_TABLE)
                .select {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeSingleOrNull<ProfileEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun uploadProfileImage(byteArray: ByteArray): Flow<String> =
        callbackFlow {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val bucket = storage.from(PROFILES_BUCKET_ID)
            val imageProfile = "$id/$PROFILE_USER_IMAGE$id$PNG_FORMAT"
            bucket.upload(imageProfile, data = byteArray) {
                upsert = true
            }
            trySend(imageProfile)
            close()
            awaitClose()
        }

    override fun updateProfileImagePath(imagePath: String): Flow<Unit> = callbackFlow {
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
        close()
        awaitClose()
    }

    override fun updateEmail(newEmail: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            auth.updateUser {
                email = newEmail
            }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun updatePhone(newPhone: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            auth.updateUser {
                phone = newPhone
            }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun updatePassword(newPassword: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            auth.updateUser {
                password = newPassword
            }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun updateNameInfo(firstName: String, lastName: String, patronymic: String): Flow<Unit> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()

        withContext(Dispatchers.IO) {
            postgrest.from(USERS_TABLE).update(
                {
                    if(firstName.isNotBlank()) set("first_name", firstName)
                    if(lastName.isNotBlank()) set("last_name", lastName)
                    if(patronymic.isNotBlank()) set("patronymic", patronymic)
                }
            ) {
                filter {
                    eq("user_id", id)
                }
            }
        }
        trySend(Unit)
        close()
        awaitClose()
    }
}