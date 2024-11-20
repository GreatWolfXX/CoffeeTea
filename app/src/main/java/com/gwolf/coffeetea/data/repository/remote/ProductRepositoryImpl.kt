package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : ProductRepository {
    override suspend fun getProducts(): Flow<UiResult<List<ProductDto>?>> = callbackFlow {
        try {
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select()
                    .decodeList<ProductDto>()
            }
            trySend(UiResult.Success(response))
            close()
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
            close()
        }
        awaitClose()
    }

    override suspend fun getProductById(productId: Int): Flow<UiResult<ProductDto?>> = callbackFlow {
        try {
            val id = auth.currentUserOrNull()?.id.orEmpty()
            val response = withContext(Dispatchers.IO) {
                postgrest.from(PRODUCTS_TABLE)
                    .select(Columns.raw("*, category: categories(category_name)")) {
                        filter {
                            eq("product_id", productId)
                        }
                    }
                    .decodeSingle<ProductDto>()
            }
            val params = JsonObject(mapOf("f_product_id" to JsonPrimitive(response.id), "f_user_id" to JsonPrimitive(id)))
            val func = postgrest.rpc("is_favorite", params) { }
            response.isFavorite = func.data.toBoolean()
            trySend(UiResult.Success(response))
            close()
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
            close()
        }
        awaitClose()
    }
}