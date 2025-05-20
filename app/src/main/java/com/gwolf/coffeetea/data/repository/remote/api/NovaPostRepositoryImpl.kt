package com.gwolf.coffeetea.data.repository.remote.api

import com.gwolf.coffeetea.BuildConfig
import com.gwolf.coffeetea.data.dto.novapost.NovaPostBody
import com.gwolf.coffeetea.data.dto.novapost.NovaPostCityDto
import com.gwolf.coffeetea.data.dto.novapost.NovaPostDepartmentsDto
import com.gwolf.coffeetea.data.dto.novapost.NovaPostProperties
import com.gwolf.coffeetea.data.dto.novapost.NovaPostResponse
import com.gwolf.coffeetea.domain.repository.remote.api.NovaPostRepository
import com.gwolf.coffeetea.util.NOVA_POST_ADDRESS_MODEL
import com.gwolf.coffeetea.util.NOVA_POST_API
import com.gwolf.coffeetea.util.NOVA_POST_GET_CITIES
import com.gwolf.coffeetea.util.NOVA_POST_GET_WAREHOUSES
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NovaPostRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : NovaPostRepository {
    override fun getCitiesBySearch(query: String): Flow<List<NovaPostCityDto>> = callbackFlow {
        val novaPostProperties = NovaPostProperties.GetCity(
            findByString = query
        )
        val novaPostBody = NovaPostBody(
            apiKey = BuildConfig.NOVA_POST_API,
            modelName = NOVA_POST_ADDRESS_MODEL,
            calledMethod = NOVA_POST_GET_CITIES,
            methodProperties = novaPostProperties
        )
        val response = httpClient.post(NOVA_POST_API) {
            contentType(ContentType.Application.Json)
            setBody(novaPostBody)
        }.body<NovaPostResponse<NovaPostCityDto>>().data
        trySend(response)
        close()
        awaitClose()
    }

    override fun getDepartments(
        typeByRef: String,
        cityRef: String,
        query: String
    ): Flow<List<NovaPostDepartmentsDto>> = callbackFlow {
        val novaPostProperties = NovaPostProperties.GetDepartments(
            cityRef = cityRef,
            typeOfWarehouseRef = typeByRef,
            findByString = query
        )
        val novaPostBody = NovaPostBody(
            apiKey = BuildConfig.NOVA_POST_API,
            modelName = NOVA_POST_ADDRESS_MODEL,
            calledMethod = NOVA_POST_GET_WAREHOUSES,
            methodProperties = novaPostProperties
        )
        val response = httpClient.post(NOVA_POST_API) {
            contentType(ContentType.Application.Json)
            setBody(novaPostBody)
        }.body<NovaPostResponse<NovaPostDepartmentsDto>>().data
        trySend(response)
        close()
        awaitClose()
    }
}