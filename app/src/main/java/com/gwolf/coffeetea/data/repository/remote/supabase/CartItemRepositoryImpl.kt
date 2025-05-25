package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.CartItemDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.util.CART_ITEMS_TABLE
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class CartItemRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth
) : CartRepository {

    override fun getCartItemProducts(): Flow<List<CartItem>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE)
                .select(Columns.raw("*, products(*)")) {
                    filter { eq("user_id", id) }
                }
                .decodeList<CartItemDto>()
        }

        val data = response.map { cartItem ->
            val imageUrl = storage.from(cartItem.product?.bucketId.orEmpty())
                .createSignedUrl(
                    cartItem.product?.imagePath.orEmpty(),
                    HOURS_EXPIRES_IMAGE_URL.hours
                )
            cartItem.toDomain(imageUrl)
        }

        emit(data)
    }

    override fun addCartItem(productId: String, quantity: Int): Flow<String?> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val cartItem = CartItemDto(
            productId = productId,
            quantity = quantity,
            userId = id
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE).insert(cartItem) {
                select(Columns.raw("*, products(*)"))
            }.decodeSingleOrNull<CartItemDto>()
        }
        emit(response?.id)
    }

    override fun removeCartItem(cartId: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE)
                .delete {
                    filter { eq("id", cartId) }
                }
        }
        emit(Unit)
    }

    override fun updateCartItemProductQuantity(cartId: String, quantity: Int): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            postgrest.from(CART_ITEMS_TABLE).update(
                { set("quantity", quantity) }
            ) {
                filter { eq("id", cartId) }
            }
        }
        emit(Unit)
    }

}