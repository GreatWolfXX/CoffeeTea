package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.CartDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getCartProducts(): Flow<UiResult<List<CartDto>?>>
    suspend fun addCart(productId: Int, quantity: Int): Flow<UiResult<Unit>>
    suspend fun removeCart(cartId: Int): Flow<UiResult<Unit>>
}