package com.gwolf.coffeetea.domain.repository.remote.api

import com.gwolf.coffeetea.data.entities.novapost.NovaPostCityEntity
import com.gwolf.coffeetea.data.entities.novapost.NovaPostDepartmentsEntity
import kotlinx.coroutines.flow.Flow

interface NovaPostRepository {
    fun getCitiesBySearch(query: String): Flow<List<NovaPostCityEntity>>
    fun getDepartments(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<List<NovaPostDepartmentsEntity>>
}