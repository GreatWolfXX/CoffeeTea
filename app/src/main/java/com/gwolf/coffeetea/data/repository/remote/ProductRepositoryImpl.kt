package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.MAX_SEARCH_LIST_RESULT
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : ProductRepository {
    override suspend fun getProducts(): Flow<List<ProductDto>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, cart: cart(*)")) {
                    filter {
                        eq("cart.user_id", id)
                    }
                }
                .decodeList<ProductDto>()
        }
        trySend(response)
        awaitClose()
    }

    override suspend fun getProductById(productId: Int): Flow<ProductDto?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, category: categories(*), favorite: favorites(*), cart: cart(*)")) {
                    filter {
                        eq("product_id", productId)
                        eq("favorites.user_id", id)
                        eq("cart.user_id", id)
                    }
                }
                .decodeSingleOrNull<ProductDto>()
        }
        trySend(response)
        awaitClose()
    }

    override suspend fun getProductsByCategory(categoryId: Int): Flow<List<ProductDto>> =
        callbackFlow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, category: categories(*)")) {
                        filter {
                            eq("category_id", categoryId)
                        }
                    }
                    .decodeList<ProductDto>()
            }
            trySend(response)
            awaitClose()
        }

    override suspend fun searchProducts(search: String): Flow<List<ProductDto>> =
        callbackFlow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, category: categories(*)")) {
                        limit(MAX_SEARCH_LIST_RESULT)
                        filter {
                            textSearch(
                                column = "product_name",
                                query = search,
                                textSearchType = TextSearchType.PHRASETO
                            )
                        }
                    }
                    .decodeList<ProductDto>()
            }
            trySend(response)
            awaitClose()
        }
}