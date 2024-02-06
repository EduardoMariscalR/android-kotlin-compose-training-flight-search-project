package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    suspend fun insert(favorite: Favorite)

    suspend fun delete(favorite: Favorite)

    fun getFavoritesList(): Flow<List<Favorite>>

    fun getAllAirports(): Flow<List<Airport>>

    fun getAirportsListFiltered(searchQuery: String): Flow<List<Airport>>

    fun getFavorite(depart: String, arrive: String): Flow<Favorite>

    fun getFavoriteAirportList(favoriteAirport : List<String>): Flow<List<Airport>>

}