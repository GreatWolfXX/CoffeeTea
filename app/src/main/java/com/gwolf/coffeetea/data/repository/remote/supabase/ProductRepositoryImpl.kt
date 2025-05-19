package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProductEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.MAX_SEARCH_LIST_RESULT
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest
) : ProductRepository {
    override fun getProducts(): Flow<List<ProductEntity>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, cart: cart(*)")) {
                    filter {
                        eq("cart.user_id", id)
                    }
                }
                .decodeList<ProductEntity>()
        }
        emit(response)
    }

    override fun getProductById(productId: Int): Flow<ProductEntity?> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*), favorites(*), cart(*)")) {
                    filter {
                        eq("product_id", productId)
                        eq("favorites.user_id", id)
                        eq("cart.user_id", id)
                    }
                    limit(1)
                }
                .decodeSingle<ProductEntity>()
        }
        emit(response)
    }

    override fun getProductsByCategory(categoryId: Int): Flow<List<ProductEntity>> = flow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*)")) {
                    filter {
                        eq("category_id", categoryId)
                    }
                }
                .decodeList<ProductEntity>()
        }
        emit(response)
    }

    override fun searchProducts(search: String): Flow<List<ProductEntity>> = flow {
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
        emit(response)
    }

    override fun getMinAndMaxProductPriceByCategory(categoryId: Int): Flow<ClosedFloatingPointRange<Float>> =
        flow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, categories(*)")) {
                        filter {
                            eq("category_id", categoryId)
                        }
                        order(column = "price", order = Order.ASCENDING)
                    }
                    .decodeList<ProductEntity>()
            }
            val priceRange = response.first().price.toFloat()..response.last().price.toFloat()
            emit(priceRange)
        }

    override fun getProductsByCategoryWithFilters(
        categoryId: Int,
        isDescending: Boolean,
        priceRange: ClosedFloatingPointRange<Float>
    ): Flow<List<ProductEntity>> = flow {
        val order = if (isDescending) Order.DESCENDING else Order.ASCENDING
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*)")) {
                    filter {
                        eq("category_id", categoryId)
                        gte("price", priceRange.start)
                        and {
                            lte("price", priceRange.endInclusive)
                        }
                    }
                    order(column = "price", order = order)
                }
                .decodeList<ProductEntity>()
        }
        Timber.d("Filter price refresh")
        emit(response)
    }
}