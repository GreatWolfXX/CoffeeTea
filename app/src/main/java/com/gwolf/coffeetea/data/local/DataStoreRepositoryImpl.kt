package com.gwolf.coffeetea.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.gwolf.coffeetea.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKey {
    val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    val rememberUserKey = booleanPreferencesKey(name = "remember_user")
}

class DataStoreRepositoryImpl(context: Context): DataStoreRepository {

    private val dataStore = context.dataStore

    override suspend fun saveBooleanState(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun readBooleanState(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if(exception is IOException) {
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