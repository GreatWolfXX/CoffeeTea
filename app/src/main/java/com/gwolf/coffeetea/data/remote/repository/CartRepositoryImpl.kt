package com.gwolf.coffeetea.data.remote.repository

import com.gwolf.coffeetea.data.entities.CartEntity
import com.gwolf.coffeetea.domain.repository.remote.CartRepository
import com.gwolf.coffeetea.util.CART_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : CartRepository {

    override suspend fun getCartProducts(): Flow<List<CartEntity>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeList<CartEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override suspend fun addCart(productId: Int, quantity: Int): Flow<Int?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val cart = CartEntity(
            productId = productId,
            quantity = quantity,
            userId = id
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_TABLE).insert(cart) {
                select()
            }.decodeSingleOrNull<CartEntity>()
        }
        trySend(response?.id)
        close()
        awaitClose()
    }

    override suspend fun removeCart(cartId: Int): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(CART_TABLE)
                .delete {
                    filter {
                        eq("cart_id", cartId)
                    }
                }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

}