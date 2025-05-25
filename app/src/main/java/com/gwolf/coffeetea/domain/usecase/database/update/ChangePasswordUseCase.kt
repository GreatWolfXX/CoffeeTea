package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(newPassword: String): Flow<DataResult<Unit>> = flow {
        try {
            profileRepository.updatePassword(newPassword)
                .collect { response ->
                    emit(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}