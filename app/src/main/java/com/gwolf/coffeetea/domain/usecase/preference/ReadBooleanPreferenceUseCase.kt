package com.gwolf.coffeetea.domain.usecase.preference

import androidx.datastore.preferences.core.Preferences
import com.gwolf.coffeetea.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadBooleanPreferenceUseCase  @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStoreRepository.readBooleanState(key)
    }
}