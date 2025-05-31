package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.entities.CartItem
import com.gwolf.coffeetea.domain.repository.remote.supabase.CartRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.OrdersRepository
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProductRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class AddOrderUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) {

    operator fun invoke(
        addressId: String,
        listCartItem: List<CartItem>
    ): Flow<DataResult<Unit>> = flow {
        try {
            val totalPrice = listCartItem.sumOf { it.product.price * it.quantity }
            val order = ordersRepository.addOrder(
                totalPrice = totalPrice,
                addressId = addressId
            ).first()

            ordersRepository.addOrderItem(
                orderId = order.id,
                listCartItems = listCartItem
            ).first()

            listCartItem.forEach { cartItem ->
                val newStockQuantity = cartItem.product.stockQuantity - cartItem.quantity
                productRepository.updateProductStockQuantity(
                    productId = cartItem.product.id,
                    stockQuantity = newStockQuantity
                ).catch { e ->
                    Timber.d("UPDATE ERROR: $e")
                }.first()

                cartRepository.removeCartItem(
                    cartId = cartItem.id
                ).first()
            }
            emit(DataResult.Success(data = Unit))
        } catch (e: Exception) {
            Timber.d("ERROR ORDER: ${e}")
            emit(DataResult.Error(exception = e))
        }
    }
}