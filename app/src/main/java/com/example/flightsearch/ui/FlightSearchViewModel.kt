package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            flightSearchRepository.getFavoritesList().collect { favoriteList ->
                userPreferencesRepository.currentSearchQuery.collect { searchQuery ->
                    flightSearchRepository.getAirportsListFiltered(searchQuery)
                        .collect { filteredAirportList ->
                            flightSearchRepository.getAllAirports().collect { airportList ->
                                val favoriteAirportList: MutableList<String> = mutableListOf()
                                favoriteList.forEach {
                                    favoriteAirportList.add(it.departureCode)
                                    favoriteAirportList.add(it.destinationCode)
                                }
                                flightSearchRepository.getFavoriteAirportList(favoriteAirportList)
                                    .collect { airportListFavorite ->
                                        _uiState.value = SearchUiState(
                                            searchQuery = searchQuery,
                                            favoriteList = favoriteList,
                                            filteredAirportList = filteredAirportList,
                                            airportList = if (searchQuery.isEmpty()) {
                                                airportListFavorite
                                            } else {
                                                airportList
                                            }
                                        )
                                    }
                            }
                        }
                }
            }
        }
    }


    fun onSearchTextValueChange(text: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveCurrentQuery(text)
            flightSearchRepository.getAirportsListFiltered(text).collect { filteredAirportList ->
                flightSearchRepository.getAllAirports().collect { airportList ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            searchQuery = text,
                            filteredAirportList = filteredAirportList,
                            airportList = airportList
                        )
                    }
                }
            }
        }
    }

    fun onSelectedAirportDepart(airport: Airport?) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = currentState.searchQuery,
                selectedAirport = airport,
                airportList = currentState.airportList,
                filteredAirportList = currentState.filteredAirportList,
                favoriteList = currentState.favoriteList
            )
        }
    }

    fun onCloseSearch() {
        viewModelScope.launch {
            userPreferencesRepository.saveCurrentQuery("")
        }
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = "",
                selectedAirport = null,
                airportList = currentState.airportList,
                filteredAirportList = emptyList(),
                favoriteList = currentState.favoriteList

            )
        }
    }

    suspend fun addFavorite(depart: String, arrive: String) {
        flightSearchRepository.insert(Favorite(departureCode = depart, destinationCode = arrive))
        flightSearchRepository.getFavoritesList().collect { favoriteList ->
            _uiState.update { currentState ->
                currentState.copy(
                    searchQuery = currentState.searchQuery,
                    selectedAirport = currentState.selectedAirport,
                    airportList = currentState.airportList,
                    filteredAirportList = currentState.filteredAirportList,
                    favoriteList = favoriteList
                )
            }
        }
    }

    suspend fun deleteFavorite(favorite: Favorite) {
        flightSearchRepository.delete(favorite)
        flightSearchRepository.getFavoritesList().collect { favoriteList ->
            _uiState.update { currentState ->
                currentState.copy(
                    searchQuery = currentState.searchQuery,
                    selectedAirport = currentState.selectedAirport,
                    airportList = currentState.airportList,
                    filteredAirportList = currentState.filteredAirportList,
                    favoriteList = favoriteList
                )
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    application.container.flightSearchRepository,
                    application.userPreferencesRepository
                )
            }
        }
    }
}

