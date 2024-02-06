package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.UserPreferencesRepository
import com.example.flightsearch.di.AppContainer
import com.example.flightsearch.di.AppDataContainer

private const val SEARCH_TEXT_PREFERENCE_NAME = "search_text_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SEARCH_TEXT_PREFERENCE_NAME)

class FlightSearchApplication: Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this,)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }

}