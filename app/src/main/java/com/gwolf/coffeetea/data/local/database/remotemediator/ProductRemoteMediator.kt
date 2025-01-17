package com.gwolf.coffeetea.data.local.database.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import com.gwolf.coffeetea.util.toLocalEntity
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val auth: Auth,
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase,
) : RemoteMediator<Int, LocalProductEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.localProductId ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val currentPage = ((page - 1) * state.config.pageSize).toLong()
            val endPage = (page * state.config.pageSize - 1).toLong()
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val response = postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, cart(*)")) {
                    filter {
                        eq("cart.user_id", id)
                    }
                    range(currentPage until endPage)
                }
                .decodeList<ProductEntity>()

            localDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDatabase.productDao.clearProducts()
                }
                val mappedResponse = response.map { product ->
                    val imageUrl = storage.from(product.bucketId)
                            .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
                    product.toLocalEntity(imageUrl)
                }
                localDatabase.productDao.addProducts(mappedResponse)
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}