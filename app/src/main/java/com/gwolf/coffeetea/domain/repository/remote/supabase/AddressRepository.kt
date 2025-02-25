package com.gwolf.coffeetea.domain.repository.remote.supabase

import com.gwolf.coffeetea.data.entities.supabase.AddressEntity
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
}