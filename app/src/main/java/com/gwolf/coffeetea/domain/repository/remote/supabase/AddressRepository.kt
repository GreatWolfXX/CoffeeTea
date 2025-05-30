package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.domain.entities.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getDeliveryAddress(addressRef: String): Flow<Address>
    fun getDeliveryAddresses(): Flow<List<Address>>

    fun addDeliveryAddress(
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Address>

    fun updateDeliveryAddress(
        addressId: String,
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Address>

    fun removeDeliveryAddress(
        addressId: String,
    ): Flow<Unit>
}