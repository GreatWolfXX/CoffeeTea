package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.FavoriteDto
import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : FavoriteRepository {
    override suspend fun getFavorites(): Flow<UiResult<List<FavoriteDto>?>> = callbackFlow {
        try {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val response = withContext(Dispatchers.IO) {
                postgrest.from(FAVORITES_TABLE)
                    .select(Columns.raw("favorite_id, products(*)")) {
                        filter {
                            eq("user_id", id)
                        }
                    }
                    .decodeList<FavoriteDto>()
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