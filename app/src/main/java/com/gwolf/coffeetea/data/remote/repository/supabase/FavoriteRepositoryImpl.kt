package com.gwolf.coffeetea.data.remote.repository.supabase

import com.gwolf.coffeetea.data.entities.supabase.FavoriteEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.FavoriteRepository
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
    private val auth: Auth,
    private val postgrest: Postgrest
) : FavoriteRepository {
    override fun getFavorites(): Flow<List<FavoriteEntity>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeList<FavoriteEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun addFavorite(productId: Int): Flow<Unit> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val favorite = FavoriteEntity(
            productId = productId,
            userId = id
        )
        withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE).insert(favorite)
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun removeFavorite(favoriteId: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(FAVORITES_TABLE)
                .delete {
                    filter {
                        eq("favorite_id", favoriteId)
                    }
                }
        }
        trySend(Unit)
        close()
        awaitClose()
    }
}