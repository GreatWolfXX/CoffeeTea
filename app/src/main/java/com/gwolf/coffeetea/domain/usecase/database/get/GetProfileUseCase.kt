package com.gwolf.coffeetea.domain.usecase.database.get

import com.gwolf.coffeetea.domain.entities.Profile
import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<DataResult<Profile>> = flow {
        try {
            profileRepository.getProfile().collect { response ->
                if (response != null) {
                    emit(DataResult.Success(data = response))
                } else {
                    emit(DataResult.Error(exception = Exception("Failed to retrieve user profile!")))
                }
            }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}