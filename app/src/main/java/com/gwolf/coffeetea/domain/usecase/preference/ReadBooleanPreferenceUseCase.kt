package com.gwolf.coffeetea.domain.usecase.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.gwolf.coffeetea.domain.repository.local.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import javax.inject.Inject

class ReadBooleanPreferenceUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStoreRepository.getDataStorePreferences()
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val state = preferences[key] ?: false
                state
            }
    }
}