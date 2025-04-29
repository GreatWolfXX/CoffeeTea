package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.AddressEntity
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DELIVERY_ADDRESSES_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AddressRepository {
    override fun getDeliveryAddresses(): Flow<List<AddressEntity>> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE)
                .select(Columns.raw("*")) {
                    filter {
                        eq("user_id", id)
                    }
                }
                .decodeList<AddressEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun addDeliveryAddress(
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Unit> = callbackFlow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val cart = AddressEntity(
            userId = id,
            deliveryType = type,
            refCity = refCity,
            refAddress = refAddress,
            city = city,
            address = address,
            isDefault = isDefault
        )
        withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE).insert(cart)
        }
        trySend(Unit)
        close()
        awaitClose()
    }

    override fun updateDeliveryAddress(
        addressId: String,
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<AddressEntity> = callbackFlow {
        val response = withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE).update(
                {
                    set("delivery_type", type)
                    set("ref_city", refCity)
                    set("ref_address", refAddress)
                    set("city", city)
                    set("address", address)
                    set("is_default", isDefault)
                }
            ) {
                select()
                filter {
                    eq("address_id", addressId)
                }
            }.decodeSingle<AddressEntity>()
        }
        trySend(response)
        close()
        awaitClose()
    }

    override fun removeDeliveryAddress(addressId: String): Flow<Unit> = callbackFlow {
        withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE)
                .delete {
                    filter {
                        eq("address_id", addressId)
                    }
                }
        }
        trySend(Unit)
        close()
        awaitClose()
    }
}