package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateNameInfoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(
        firstName: String = "",
        lastName: String = "",
        patronymic: String = ""
    ): Flow<DataResult<Unit>> = flow {
        try {
            profileRepository.updateNameInfo(firstName, lastName, patronymic)
                .collect { response ->
                    emit(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            emit(DataResult.Error(exception = e))
        }
    }
}