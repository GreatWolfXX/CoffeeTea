package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.data.toDomain
import com.gwolf.coffeetea.domain.entities.Address
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(): Flow<DataResult<List<Address>>> = callbackFlow {
        try {
            addressRepository.getDeliveryAddresses().collect { response ->
                val data = response.map { address ->
                    return@map address.toDomain()
                }
                trySend(DataResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}