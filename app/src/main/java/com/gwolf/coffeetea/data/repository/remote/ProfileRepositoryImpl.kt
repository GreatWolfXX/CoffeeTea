package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.domain.repository.remote.ProfileRepository
import com.gwolf.coffeetea.util.PROFILE_TABLE
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : ProfileRepository {
    override suspend fun getProfile(): Flow<UiResult<ProfileDto?>> = callbackFlow {
        try {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PROFILE_TABLE)
                    .select{
                        filter {
                            eq("user_id", id)
                        }
                    }
                    .decodeSingleOrNull<ProfileDto>()
            }
            trySend(UiResult.Success(response))
            close()
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
            close()
        }
        awaitClose()
    }
}