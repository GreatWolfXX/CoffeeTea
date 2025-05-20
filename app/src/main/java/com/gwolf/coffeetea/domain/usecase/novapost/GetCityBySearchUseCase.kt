package com.gwolf.coffeetea.domain.usecase.novapost

import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.data.toDomain
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetCityBySearchUseCase @Inject constructor(
    private val novaPostRepository: NovaPostRepository
) {
    operator fun invoke(query: String): Flow<DataResult<List<City>>> = callbackFlow {
        try {
            novaPostRepository.getCitiesBySearch(query)
                .collect { response ->
                    val data = response.map { city ->
                        return@map city.toDomain()
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