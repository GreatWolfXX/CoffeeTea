package com.gwolf.coffeetea.domain.usecase.preference

import androidx.datastore.preferences.core.Preferences
import com.gwolf.coffeetea.domain.repository.DataStoreRepository
import javax.inject.Inject

class SaveBooleanPreferenceUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStoreRepository.saveBooleanState(key, value)
    }
}