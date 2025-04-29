package com.gwolf.coffeetea.domain.usecase.database.update

import com.gwolf.coffeetea.domain.repository.remote.supabase.ProfileRepository
import com.gwolf.coffeetea.util.DataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UpdateNameInfoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(firstName: String = "", lastName: String = "", patronymic: String = ""): Flow<DataResult<Unit>> = callbackFlow {
        try {
            profileRepository.updateNameInfo(firstName, lastName, patronymic)
                .collect { response ->
                    trySend(DataResult.Success(data = response))
                }
        } catch (e: Exception) {
            trySend(DataResult.Error(exception = e))
        } finally {
            close()
        }
        awaitClose()
    }
}