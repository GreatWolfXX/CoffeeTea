package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CartItemEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.CART_ITEMS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartItemRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : CartRepository {

    override fun getCartItemProducts(): Flow<List<CartItemEntity>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeList<CartItemEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun addCartItem(productId: String, quantity: Int): Flow<String?> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val cartItem = CartItemEntity(
            productId = productId,
            quantity = quantity,
            userId = id
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE).insert(cartItem) {
                select(Columns.raw("*, products(*)"))
            }.decodeSingleOrNull<CartItemEntity>()
        }
        trySend(response?.id)
        close()
        awaitClose()
    }

    override fun removeCartItem(cartId: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE)
                .delete {
                    filter {
                        eq("id", cartId)
                    }
                }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun updateCartItemProductQuantity(cartId: String, quantity: Int): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE).update(
                {
                    set("quantity", quantity)
                }
            ) {
                filter {
                    eq("id", cartId)
                }
            }
        }
        trySend(Unit)
        close()
        awaitClose()
    }

}