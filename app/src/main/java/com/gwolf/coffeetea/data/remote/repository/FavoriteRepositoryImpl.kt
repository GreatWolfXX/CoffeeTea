package com.gwolf.coffeetea.data.remote.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.FavoriteWithProductEntity
import com.gwolf.coffeetea.data.local.database.remotemediator.FavoriteRemoteMediator
import com.gwolf.coffeetea.domain.repository.remote.FavoriteRepository
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase
) : FavoriteRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getFavorites(): Flow<PagingData<FavoriteWithProductEntity>> {
        val pagingSourceFactory = { localDatabase.favoriteDao.getFavorites() }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = FavoriteRemoteMediator(auth, storage, postgrest, localDatabase),
            pagingSourceFactory = pagingSourceFactory
        ).flow
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