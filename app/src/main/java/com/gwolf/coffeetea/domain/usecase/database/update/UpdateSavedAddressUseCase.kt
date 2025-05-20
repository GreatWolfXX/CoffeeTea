package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.data.toDomain
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UpdateSavedAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(
        addressId: String,
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<DataResult<Address>> = callbackFlow {
        try {
            addressRepository.updateDeliveryAddress(
                addressId = addressId,
                type = type,
                refCity = refCity,
                refAddress = refAddress,
                city = city,
                address = address,
                isDefault = isDefault
            ).collect { response ->
                trySend(DataResult.Success(data = response.toDomain()))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}