package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoveSavedDeliveryUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(addressId: String): Flow<DataResult<Unit>> = callbackFlow {
        try {
            addressRepository.removeDeliveryAddress(addressId).collect { response ->
                trySend(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}