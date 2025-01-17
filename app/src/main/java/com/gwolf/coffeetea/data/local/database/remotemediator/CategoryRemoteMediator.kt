package com.gwolf.coffeetea.data.local.database.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gwolf.coffeetea.data.entities.CategoryEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalCategoryEntity
import com.gwolf.coffeetea.util.CATEGORIES_TABLE
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.toLocalEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalPagingApi::class)
class CategoryRemoteMediator(
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase,
) : RemoteMediator<Int, LocalCategoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalCategoryEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.localCategoryId ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val currentPage = ((page - 1) * state.config.pageSize).toLong()
            val endPage = (page * state.config.pageSize - 1).toLong()
            val response = postgrest.from(CATEGORIES_TABLE)
                .select {
                    range(currentPage until endPage)
                }
                .decodeList<CategoryEntity>()

            localDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(LOGGER_TAG, "category update internet")
                    localDatabase.categoryDao.clearCategories()
                }
                val mappedResponse = response.map { category ->
                    val imageUrl = storage.from(category.bucketId)
                        .createSignedUrl(category.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    category.toLocalEntity(imageUrl)
                }
                localDatabase.categoryDao.addCategories(mappedResponse)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}