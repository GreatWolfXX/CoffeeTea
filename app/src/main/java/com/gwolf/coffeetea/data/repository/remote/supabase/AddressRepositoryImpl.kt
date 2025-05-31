package com.gwolf.coffeetea.data.repository.remote.supabase

import com.gwolf.coffeetea.data.dto.supabase.AddressDto
import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.data.toListAddressDomain
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DELIVERY_ADDRESSES_TABLE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AddressRepository {

    override fun getDeliveryAddress(
        addressRef: String
    ): Flow<Address> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE)
                .select(Columns.raw("*")) {
                    filter {
                        eq("user_id", id)
                        eq("ref_address", addressRef)
                    }
                }
                .decodeSingle<AddressDto>()
        }
        emit(response.toDomain())
    }

    override fun getDeliveryAddresses(): Flow<List<Address>> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val response = withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE)
                .select(Columns.raw("*")) {
                    filter { eq("user_id", id) }
                }
                .decodeList<AddressDto>()
        }
        emit(response.toListAddressDomain())
    }

    override fun addDeliveryAddress(
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Address> = flow {
        val id = auth.currentUserOrNull()?.id.orEmpty()
        val address = AddressDto(
            userId = id,
            deliveryType = type,
            refCity = refCity,
            refAddress = refAddress,
            city = city,
            address = address,
            isDefault = isDefault
        )
        val response = withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE).insert(address) {
                select()
            }.decodeSingle<AddressDto>()
        }
        emit(response.toDomain())
    }

    override fun updateDeliveryAddress(
        addressId: String,
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<Address> = flow {
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
                filter { eq("id", addressId) }
            }.decodeSingle<AddressDto>()
        }
        emit(response.toDomain())
    }

    override fun removeDeliveryAddress(addressId: String): Flow<Unit> = flow {
        withContext(Dispatchers.IO) {
            postgrest.from(DELIVERY_ADDRESSES_TABLE)
                .delete {
                    filter { eq("id", addressId) }
                }
        }
        emit(Unit)
    }
}