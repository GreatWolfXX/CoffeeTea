package com.gwolf.coffeetea.data.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.gwolf.coffeetea.domain.repository.local.DataStoreRepository
import kotlinx.coroutines.flow.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKey {
    val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    val rememberUserKey = booleanPreferencesKey(name = "remember_user")
}

class DataStoreRepositoryImpl(context: Context): DataStoreRepository {

    private val dataStore = context.dataStore

    override suspend fun <T> saveState(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun getDataStorePreferences(): Flow<Preferences> {
        return dataStore.data
    }
}