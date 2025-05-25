package com.gwolf.coffeetea.domain.repository.remote.api

import com.gwolf.coffeetea.domain.entities.City
import com.gwolf.coffeetea.domain.entities.Department
import kotlinx.coroutines.flow.Flow

interface NovaPostRepository {
    fun getCitiesBySearch(query: String): Flow<List<City>>
    fun getDepartments(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<List<Department>>
}