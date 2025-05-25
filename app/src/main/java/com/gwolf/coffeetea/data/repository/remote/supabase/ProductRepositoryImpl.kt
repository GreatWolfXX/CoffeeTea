package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.ProductDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Product
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.MAX_SEARCH_LIST_RESULT
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class ProductRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val storage: Storage
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, cart_items(*)")) {
                    filter { eq("cart_items.user_id", id) }
                }
                .decodeList<ProductDto>()
        }

        val data = response.map { product ->
            val imageUrl = storage.from(product.bucketId)
                .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            product.toDomain(imageUrl)
        }

        emit(data)
    }

    override fun getProductById(productId: String): Flow<Product?> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*), favorites(*), cart_items(*)")) {
                    filter {
                        eq("id", productId)
                        eq("favorites.user_id", id)
                        eq("cart_items.user_id", id)
                    }
                    limit(1)
                }
                .decodeSingle<ProductDto>()
        }

        val imageUrl = storage.from(response.bucketId)
            .createSignedUrl(response.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)

        emit(response.toDomain(imageUrl))
    }

    override fun getProductsByCategory(categoryId: String): Flow<List<Product>> = flow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(PRODUCTS_TABLE)
                .select(Columns.raw("*, categories(*)")) {
                    filter { eq("category_id", categoryId) }
                }
                .decodeList<ProductDto>()
        }

        val data = response.map { product ->
            val imageUrl = storage.from(product.bucketId)
                .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            return@map product.toDomain(imageUrl)
        }

        emit(data)
    }

    override fun searchProducts(search: String): Flow<List<Product>> = flow {
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
                .decodeList<ProductDto>()
        }

        val data = response.map { product ->
            val imageUrl = storage.from(product.bucketId)
                .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            product.toDomain(imageUrl)
        }

        emit(data)
    }

    override fun getMinAndMaxProductPriceByCategory(categoryId: String): Flow<ClosedFloatingPointRange<Float>> =
        flow {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, categories(*)")) {
                        filter {
                            eq("category_id", categoryId)
                        }
                        order(column = "price", order = Order.ASCENDING)
                    }
                    .decodeList<ProductDto>()
            }
            val priceRange = response.first().price.toFloat()..response.last().price.toFloat()
            emit(priceRange)
        }

    override fun getProductsByCategoryWithFilters(
        categoryId: String,
        isDescending: Boolean,
        priceRange: ClosedFloatingPointRange<Float>
    ): Flow<List<Product>> = flow {
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
                .decodeList<ProductDto>()
        }

        val data = response.map { product ->
            val imageUrl = storage.from(product.bucketId)
                .createSignedUrl(product.imagePath, HOURS_EXPIRES_IMAGE_URL.hours)
            return@map product.toDomain(imageUrl)
        }

        emit(data)
    }
}