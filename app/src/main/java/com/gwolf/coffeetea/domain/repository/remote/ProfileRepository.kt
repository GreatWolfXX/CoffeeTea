package com.gwolf.coffeetea.domain.repository.remote

import com.gwolf.coffeetea.data.dto.ProfileDto
import com.gwolf.coffeetea.util.UiResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<UiResult<ProfileDto?>>
}