package com.gwolf.coffeetea.data.local.database.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gwolf.coffeetea.data.entities.PromotionEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalPromotionEntity
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.PROMOTIONS_TABLE
import com.gwolf.coffeetea.util.toLocalEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalPagingApi::class)
class PromotionRemoteMediator(
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase,
) : RemoteMediator<Int, LocalPromotionEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalPromotionEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.localPromotionId ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val currentPage = ((page - 1) * state.config.pageSize).toLong()
            val endPage = (page * state.config.pageSize - 1).toLong()
            val response = postgrest.from(PROMOTIONS_TABLE)
                .select {
                    range(currentPage until endPage)
                }
                .decodeList<PromotionEntity>()

            localDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDatabase.promotionDao.clearPromotions()
                }
                val mappedResponse = response.map { promotion ->
                    val imageUrl = storage.from(promotion.bucketId)
                        .createSignedUrl(promotion.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    promotion.toLocalEntity(imageUrl)
                }
                localDatabase.promotionDao.addPromotions(mappedResponse)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}