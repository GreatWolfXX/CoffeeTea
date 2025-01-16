package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.FavoriteDto
import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.FAVORITES_TABLE
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
    override suspend fun getFavorites(): Flow<List<FavoriteDto>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeList<FavoriteDto>()
        }
        trySend(response)
        awaitClose()
    }

    override suspend fun addFavorite(productId: Int): Flow<Unit> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val favorite = FavoriteDto(
            productId = productId,
            userId = id
        )
        withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE).insert(favorite)
        }
        trySend(Unit)
        awaitClose()
    }

    override suspend fun removeFavorite(favoriteId: Int): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .delete {
                    filter {
                        eq("favorite_id", favoriteId)
                    }
                }
        }
        trySend(Unit)
        awaitClose()
    }
}