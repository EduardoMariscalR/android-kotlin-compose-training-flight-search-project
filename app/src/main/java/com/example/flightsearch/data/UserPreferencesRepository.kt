package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val currentSearchQuery: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[CURRENT_SEARCH_QUERY] ?: ""
        }

    private companion object {
        val CURRENT_SEARCH_QUERY = stringPreferencesKey("")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveCurrentQuery(currentSearchQuery: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_SEARCH_QUERY] = currentSearchQuery
        }
    }
}