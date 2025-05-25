package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.AddressRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(
        type: String,
        refCity: String,
        refAddress: String,
        city: String,
        address: String,
        isDefault: Boolean
    ): Flow<DataResult<Unit>> = flow {
        try {
            addressRepository.addDeliveryAddress(
                type,
                refCity,
                refAddress,
                city,
                address,
                isDefault
            ).collect { response ->
                emit(DataResult.Success(data = response))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}