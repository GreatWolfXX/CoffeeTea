package com.gwolf.coffeetea.domain.usecase.novapost

import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.util.NOVA_POST_CARGO_DEPARTMENT_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.data.toDomain
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetDepartmentsUseCase @Inject constructor(
    private val novaPostRepository: NovaPostRepository
) {
    operator fun invoke(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<DataResult<List<Department>>> = callbackFlow {
        try {
            if (typeByRef == NOVA_POST_DEPARTMENT_REF) {
                val cargoDepartments = novaPostRepository.getDepartments(
                    NOVA_POST_CARGO_DEPARTMENT_REF,
                    cityRef,
                    query
                ).onEach { delay(2500) }
                novaPostRepository.getDepartments(NOVA_POST_DEPARTMENT_REF, cityRef, query).combine(
                    cargoDepartments
                ) { responseDepartment, responseCargoDepartment ->
                    responseCargoDepartment + responseDepartment
                }.collect { response ->
                    val data = response.map { department ->
                        return@map department.toDomain()
                    }
                    trySend(DataResult.Success(data = data))
                }
            } else {
                novaPostRepository.getDepartments(typeByRef, cityRef, query)
                    .collect { response ->
                        val data = response.map { department ->
                            return@map department.toDomain()
                        }
                        trySend(DataResult.Success(data = data))
                    }
            }

        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}