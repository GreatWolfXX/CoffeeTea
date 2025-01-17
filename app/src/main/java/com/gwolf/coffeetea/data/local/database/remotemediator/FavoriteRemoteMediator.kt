package com.gwolf.coffeetea.data.local.database.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gwolf.coffeetea.data.entities.FavoriteEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.FavoriteWithProductEntity
import com.gwolf.coffeetea.util.FAVORITES_TABLE
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.toLocalEntity
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalPagingApi::class)
class FavoriteRemoteMediator(
    private val auth: Auth,
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase,
) : RemoteMediator<Int, FavoriteWithProductEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FavoriteWithProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.favorite?.localFavoriteId ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val currentPage = ((page - 1) * state.config.pageSize).toLong()
            val endPage = (page * state.config.pageSize - 1).toLong()
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val response = postgrest.from(FAVORITES_TABLE)
                .select(Columns.raw("*, products(product_id, bucket_id, image_path)")) {
                    filter {
                        eq("user_id", id)
                    }
                    range(currentPage until endPage)
                }
                .decodeList<FavoriteEntity>()

            localDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDatabase.favoriteDao.clearFavorites()
                }
                val mappedResponse = response.map { favorite ->
                    val productImageUrl = storage.from(favorite.product?.bucketId.orEmpty())
                            .createSignedUrl(
                                favorite.product?.imagePath.orEmpty(),
                                HOURS_EXPIRES_IMAGE_URL.hours
                            )
                    favorite.toLocalEntity(productImageUrl)
                }
                localDatabase.favoriteDao.addFavorites(mappedResponse)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}