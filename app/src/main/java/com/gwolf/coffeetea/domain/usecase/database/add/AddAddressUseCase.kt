package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(type: String, refCity: String, refAddress: String, city: String, address: String, isDefault: Boolean): Flow<UiResult<Unit>> = callbackFlow {
        try {
            addressRepository.addDeliveryAddress(type, refCity, refAddress, city, address, isDefault).collect { response ->
                if (response != null) {
                    trySend(UiResult.Success(data = response))
                } else {
                    trySend(UiResult.Error(exception = Exception("Save address failed!")))
                }
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}