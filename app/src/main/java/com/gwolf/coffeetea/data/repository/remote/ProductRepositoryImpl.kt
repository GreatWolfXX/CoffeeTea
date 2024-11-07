package com.gwolf.coffeetea.data.repository.remote

import com.gwolf.coffeetea.data.dto.ProductDto
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.PRODUCTS_TABLE
import com.gwolf.coffeetea.util.UiResult
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
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
}