package com.gwolf.coffeetea.domain.usecase.novapost

import com.gwolf.coffeetea.domain.entities.Department
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.util.DataResult
import com.gwolf.coffeetea.util.NOVA_POST_CARGO_DEPARTMENT_REF
import com.gwolf.coffeetea.util.NOVA_POST_DEPARTMENT_REF
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetDepartmentsUseCase @Inject constructor(
    private val novaPostRepository: NovaPostRepository
) {
    operator fun invoke(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<DataResult<List<Department>>> = flow {
        try {
            when (typeByRef) {
                NOVA_POST_DEPARTMENT_REF -> {
                    val cargoDepartments = novaPostRepository.getDepartments(
                        NOVA_POST_CARGO_DEPARTMENT_REF,
                        cityRef,
                        query
                    ).onEach { delay(2500) }

                    novaPostRepository.getDepartments(NOVA_POST_DEPARTMENT_REF, cityRef, query)
                        .combine(
                            cargoDepartments
                        ) { responseDepartment, responseCargoDepartment ->
                            responseCargoDepartment + responseDepartment
                        }.collect { response ->
                            emit(DataResult.Success(data = response))
                        }
                }

                else -> {
                    novaPostRepository.getDepartments(typeByRef, cityRef, query)
                        .collect { response ->
                            emit(DataResult.Success(data = response))
                        }
                }
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}