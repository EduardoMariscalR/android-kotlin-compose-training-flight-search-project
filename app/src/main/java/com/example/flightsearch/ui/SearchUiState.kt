package com.example.flightsearch.ui

import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite

data class SearchUiState(
    val searchQuery: String = "",
    val selectedAirport: Airport? = null,
    val airportList: List<Airport> = emptyList(),
    val filteredAirportList: List<Airport> = emptyList(),
    val favoriteList: List<Favorite> = emptyList()
)
