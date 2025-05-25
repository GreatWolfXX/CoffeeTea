package com.gwolf.coffeetea.domain.usecase.database.remove

import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveSavedDeliveryUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(addressId: String): Flow<DataResult<Unit>> = flow {
        try {
            addressRepository.removeDeliveryAddress(addressId).collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}