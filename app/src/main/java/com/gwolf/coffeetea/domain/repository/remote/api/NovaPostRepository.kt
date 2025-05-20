package com.gwolf.coffeetea.domain.repository.remote.api

import com.gwolf.coffeetea.data.dto.novapost.NovaPostCityDto
import com.gwolf.coffeetea.data.dto.novapost.NovaPostDepartmentsDto
import kotlinx.coroutines.flow.Flow

interface NovaPostRepository {
    fun getCitiesBySearch(query: String): Flow<List<NovaPostCityDto>>
    fun getDepartments(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<List<NovaPostDepartmentsDto>>
}