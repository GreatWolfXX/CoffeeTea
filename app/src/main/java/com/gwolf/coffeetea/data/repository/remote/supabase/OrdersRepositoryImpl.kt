package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.OrderDto
import com.gwolf.coffeetea.data.dto.supabase.OrderItemDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.entities.Order
import com.gwolf.coffeetea.domain.repository.remote.supabase.OrdersRepository
import com.gwolf.coffeetea.util.HOURS_EXPIRES_IMAGE_URL
import com.gwolf.coffeetea.util.ORDERS_TABLE
import com.gwolf.coffeetea.util.ORDER_ITEMS_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class OrdersRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val storage: Storage
) : OrdersRepository {

    override fun getOrders(): Flow<List<Order>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(ORDERS_TABLE)
                .select(Columns.raw("*, order_items(*), delivery_addresses(*)")) {
                    filter { eq("user_id", id) }
                }
                .decodeList<OrderDto>()
        }

        val data = response.map { order ->
            val listOrderItem = order.orderItems.map { orderItem ->
                val imageUrl = storage.from(orderItem.product?.bucketId.orEmpty())
                    .createSignedUrl(
                        orderItem.product?.imagePath.orEmpty(),
                        HOURS_EXPIRES_IMAGE_URL.hours
                    )
                orderItem.toDomain(imageUrl)
            }
            order.toDomain(listOrderItem)
        }

        emit(data)
    }

    override fun addOrder(totalPrice: Double, addressId: String): Flow<Order> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()

        val order = OrderDto(
            userId = id,
            addressId = addressId,
            totalPrice = totalPrice
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(ORDERS_TABLE).insert(order) {
                select(Columns.raw("*, order_items(*), delivery_addresses(*)"))
            }.decodeSingle<OrderDto>()
        }
        emit(response.toDomain())
    }

    override fun addOrderItem(
        orderId: String,
        listCartItems: List<CartItem>
    ): Flow<Unit> =
        flow {
            val order = listCartItems.map {
                OrderItemDto(
                    orderId = orderId,
                    productId = it.product.id,
                    quantity = it.quantity
                )
            }
            Timber.d("ORDERS: $order")
            withContext(Dispatchers.IO) {
                postgrest.from(ORDER_ITEMS_TABLE).insert(order)
            }
            emit(Unit)
        }

    override fun removeOrder(orderId: String): Flow<Unit> =  flow {
        withContext(Dispatchers.IO) {
            postgrest.from(ORDER_ITEMS_TABLE).delete {
                filter { eq("id", orderId) }
            }
        }
        emit(Unit)
    }
}