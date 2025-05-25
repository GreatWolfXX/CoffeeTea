package com.gwolf.coffeetea.domain.usecase.novapost

import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCityBySearchUseCase @Inject constructor(
    private val novaPostRepository: NovaPostRepository
) {
    operator fun invoke(query: String): Flow<DataResult<List<City>>> = flow {
        try {
            novaPostRepository.getCitiesBySearch(query)
                .collect { response ->
                    emit(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}