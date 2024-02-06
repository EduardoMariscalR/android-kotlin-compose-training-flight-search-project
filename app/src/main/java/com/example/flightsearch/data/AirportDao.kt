package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%' ORDER BY passengers DESC ")
    fun getAirportsListFiltered(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code IN (:favoriteAirport) ORDER BY passengers DESC")
    fun getFavoriteAirportList(favoriteAirport : List<String>): Flow<List<Airport>>
}