package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class FlightSearchRepositoryImpl(private val airportDao: AirportDao, private val favoriteDao: FavoriteDao) : FlightSearchRepository {
    override suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    override suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)

    override fun getFavoritesList(): Flow<List<Favorite>> = favoriteDao.getFavoriteList()

    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    override fun getAirportsListFiltered(searchQuery: String): Flow<List<Airport>> = airportDao.getAirportsListFiltered(searchQuery)

    override fun getFavorite(depart: String, arrive: String): Flow<Favorite> = favoriteDao.getFavorite(depart,arrive)

    override fun getFavoriteAirportList(favoriteAirport: List<String>): Flow<List<Airport>> = airportDao.getFavoriteAirportList(favoriteAirport)

}