package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.model.Address
import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.UiResult
import com.gwolf.coffeetea.util.toDomain
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetAddressListUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
    private val storage: Storage
) {
    operator fun invoke(): Flow<UiResult<List<Address>>> = callbackFlow {
        try {
            addressRepository.getDeliveryAddresses().collect { response ->
                val data = response.map { address ->
                    return@map address.toDomain()
                }
                trySend(UiResult.Success(data = data))
            }
        } catch (e: Exception) {
            trySend(UiResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}