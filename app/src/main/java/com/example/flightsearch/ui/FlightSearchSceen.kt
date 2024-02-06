package com.example.flightsearch.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.ui.theme.FlightSearchTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightSearchViewModel = viewModel(
        factory = FlightSearchViewModel.Factory
    )
) {
    val uiState: SearchUiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    if (uiState.searchQuery.isNotEmpty()) {
        text = uiState.searchQuery
        val currentAirportSearch: Airport? =
            uiState.airportList.find { airport -> airport.iataCode == uiState.searchQuery }
        if (null != currentAirportSearch) {
            viewModel.onSelectedAirportDepart(uiState.airportList.find { airport -> airport.iataCode == uiState.searchQuery })
        }
    }

    Log.i("Eduardo", "Current query: ${uiState.searchQuery}")


    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            modifier = Modifier,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.DarkGray, titleContentColor = Color.White
            ),
            scrollBehavior = scrollBehavior
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SearchBar(
                query = text,
                onQueryChange = {
                    text = it
                    viewModel.onSearchTextValueChange(text)
                },
                onSearch = {
                    active = false
                },
                active = active,
                onActiveChange = {
                    if (!active) {
                        viewModel.onSearchTextValueChange(text)
                    }
                    active = it
                },
                placeholder = {
                    Text(text = "Enter your query")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
                },
                trailingIcon = {
                    if (text.isNotEmpty() || active) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                    viewModel.onCloseSearch()
                                    active = false
                                } else {
                                    active = false
                                }
                            }, imageVector = Icons.Default.Close, contentDescription = "Close icon"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
            ) {
                LazyColumn {
                    items(items = uiState.filteredAirportList,
                        key = { airport -> airport.id }) { airport ->
                        Row(modifier = Modifier
                            .padding(all = 14.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onSearchTextValueChange(airport.iataCode)
                                viewModel.onSelectedAirportDepart(airport)
                                active = false
                            }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = airport.iataCode)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = airport.name)
                        }
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                if (uiState.selectedAirport == null) {
                    if (uiState.favoriteList.isNotEmpty()) {
                        item {
                            Text(
                                text = "Favorite routes",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Flights from ${uiState.selectedAirport!!.iataCode}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
                if (uiState.selectedAirport == null) {
                    items(items = uiState.favoriteList) { airport ->
                        val depart: Airport =
                            uiState.airportList.find { it.iataCode == airport.departureCode }!!
                        val arrive: Airport =
                            uiState.airportList.find { it.iataCode == airport.destinationCode }!!
                        FlyCard(
                            depart = depart,
                            arrive = arrive,
                            favoriteList = uiState.favoriteList,
                            coroutineScope = coroutineScope,
                            viewModel = viewModel
                        )
                    }
                } else {
                    items(items = uiState.airportList.filterNot { airport -> uiState.selectedAirport == airport },
                        key = { airport -> airport.id }) { airport ->
                        FlyCard(
                            depart = uiState.selectedAirport!!,
                            arrive = airport,
                            favoriteList = uiState.favoriteList,
                            coroutineScope = coroutineScope,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FlyCard(
    depart: Airport,
    arrive: Airport,
    favoriteList: List<Favorite>,
    coroutineScope: CoroutineScope,
    viewModel: FlightSearchViewModel

){
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text("DEPART", fontStyle = FontStyle.Italic)
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Text(
                        depart.iataCode.uppercase(),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        depart.name,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Text("ARRIVE", fontStyle = FontStyle.Italic)
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Text(
                        arrive.iataCode.uppercase(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        arrive.name,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            val currentFavorite: Favorite? =
                favoriteList.find { favorite ->
                    favorite.departureCode == depart.iataCode && favorite.destinationCode == arrive.iataCode
                }
            IconButton(
                onClick = {
                    if (currentFavorite != null) {
                        coroutineScope.launch {
                            viewModel.run { deleteFavorite(currentFavorite) }
                        }
                    } else {
                        coroutineScope.launch {
                            viewModel.addFavorite(
                                depart = depart.iataCode,
                                arrive = arrive.iataCode
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.1f)
            ) {
                if (currentFavorite != null) {
                    Icon(
                        Icons.Default.Favorite, Icons.Default.Favorite.name
                    )
                } else {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        Icons.Default.FavoriteBorder.name
                    )
                }
            }
        }
    }

}
