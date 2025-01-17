package com.gwolf.coffeetea.data.remote.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gwolf.coffeetea.data.entities.ProductEntity
import com.gwolf.coffeetea.data.local.database.LocalDatabase
import com.gwolf.coffeetea.data.local.database.entities.LocalProductEntity
import com.gwolf.coffeetea.data.local.database.remotemediator.ProductRemoteMediator
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.MAX_SEARCH_LIST_RESULT
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val storage: Storage,
    private val postgrest: Postgrest,
    private val localDatabase: LocalDatabase
) : ProductRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(): Flow<PagingData<LocalProductEntity>> {
        val pagingSourceFactory = { localDatabase.productDao.getProducts() }

        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = ProductRemoteMediator(auth, storage, postgrest, localDatabase),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getProductById(productId: Int): Flow<ProductEntity?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*), favorites(*), cart(*)")) {
                    filter {
                        eq("product_id", productId)
                        eq("favorites.user_id", id)
                        eq("cart.user_id", id)
                    }
                }
                .decodeSingleOrNull<ProductEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>> =
        callbackFlow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, categories(*)")) {
                        filter {
                            eq("category_id", categoryId)
                        }
                    }
                    .decodeList<ProductEntity>()
            }
            trySend(response)
            close()
            awaitClose()
        }

    override fun searchProducts(search: String): Flow<List<ProductEntity>> =
        callbackFlow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, categories(*)")) {
                        limit(MAX_SEARCH_LIST_RESULT)
                        filter {
                            textSearch(
                                column = "product_name",
                                query = search,
                                textSearchType = TextSearchType.PHRASETO
                            )
                        }
                    }
                    .decodeList<ProductEntity>()
            }
            trySend(response)
            close()
            awaitClose()
        }
}