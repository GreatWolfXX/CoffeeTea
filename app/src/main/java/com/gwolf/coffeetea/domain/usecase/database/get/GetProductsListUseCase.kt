package com.gwolf.coffeetea.domain.usecase.database.get

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.gwolf.coffeetea.domain.model.Product
import com.gwolf.coffeetea.domain.repository.remote.ProductRepository
import com.gwolf.coffeetea.util.LOGGER_TAG
import com.gwolf.coffeetea.util.toDomain
import com.gwolf.coffeetea.util.toEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetProductsListUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<PagingData<Product>> = callbackFlow {
        try {
            productRepository.getProducts().collect { response ->
                val data = response.map { productPagingData ->
                    val product = productPagingData.toEntity()
                    return@map product.toDomain(productPagingData.imageUrl)
                }
                trySend(data)
            }
        } catch (e: Exception) {
            Log.d(LOGGER_TAG, "Product Paging Data Error! : $e")
        } finally {
            close()
        }
        awaitClose()
    }
}