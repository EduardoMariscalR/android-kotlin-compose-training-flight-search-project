package com.example.flightsearch.di

import android.content.Context
import com.example.flightsearch.data.FlightSearchDatabase
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.FlightSearchRepositoryImpl

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
}

class AppDataContainer(private val context: Context) : AppContainer {


    override val flightSearchRepository: FlightSearchRepository by lazy{
        FlightSearchRepositoryImpl(
            FlightSearchDatabase.getDatabase(context).airportDao(),
            FlightSearchDatabase.getDatabase(context).favoriteDao())
    }
}