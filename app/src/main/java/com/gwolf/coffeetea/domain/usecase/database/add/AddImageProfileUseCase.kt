package com.gwolf.coffeetea.domain.usecase.database.add

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddImageProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(byteArray: ByteArray): Flow<DataResult<String>> =
        flow {
            try {
                profileRepository.uploadProfileImage(byteArray).collect { response ->
                    emit(DataResult.Success(data = response))
                }
            } catch (e: Exception) {
                emit(DataResult.Error(exception = e))
            }
        }
}