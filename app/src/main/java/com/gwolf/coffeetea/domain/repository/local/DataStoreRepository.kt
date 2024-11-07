package com.gwolf.coffeetea.domain.repository.local

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun <T> saveState(key: Preferences.Key<T>, value: T)
    fun getDataStorePreferences(): Flow<Preferences>
}