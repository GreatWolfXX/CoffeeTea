package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.AddressEntity
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getDeliveryAddresses(): Flow<List<AddressEntity>>
    fun addDeliveryAddress(
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Unit>
    fun updateDeliveryAddress(
        addressId: String,
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<AddressEntity>
    fun removeDeliveryAddress(
        addressId: String,
    ): Flow<Unit>
}