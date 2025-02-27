package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.model.Address
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
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
    ): Flow<UiResult<Address>> = callbackFlow {
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
                trySend(UiResult.Success(data = response.toDomain()))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}