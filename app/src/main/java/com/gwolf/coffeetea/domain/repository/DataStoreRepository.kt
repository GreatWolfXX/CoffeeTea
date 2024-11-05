package com.gwolf.coffeetea.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveBooleanState(key: Preferences.Key<Boolean>, value: Boolean)
    fun readBooleanState(key: Preferences.Key<Boolean>): Flow<Boolean>
}