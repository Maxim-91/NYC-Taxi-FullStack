package com.example.nyctaxiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** ViewModel responsible for managing the state and business logic of the Locations Screen.
 *  It handles data fetching, complex search logic, and multi-criteria filtering. **/
class LocationsViewModel : ViewModel() {

    // --- RAW DATA STATE HOLDERS ---
    // Using MutableStateFlow to hold data fetched from the API
    private val _allZones = MutableStateFlow<List<Zone>>(emptyList())
    private val _boroughs = MutableStateFlow<List<String>>(emptyList())
    private val _serviceZones = MutableStateFlow<List<String>>(emptyList())

    // --- UI STATE FLOWS ---
    // Holds the current search string entered by the user
    val searchQuery = MutableStateFlow("")

    // Maps to track the 'checked' state (on/off) for each individual filter item
    val selectedBoroughs = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val selectedServiceZones = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    // Reactive boolean flows that determine if all items in a group are selected.
    // This is used to control the state and enabling of the "All" checkbox in the UI.
    val isAllBoroughsOn = selectedBoroughs.map { map -> map.values.all { it } }
    val isAllServiceZonesOn = selectedServiceZones.map { map -> map.values.all { it } }

    init {
        // Fetch data immediately when the ViewModel is created
        loadInitialData()
    }

    /** Fetches initialization data from the API and sets the default state of all filters to 'on'. **/
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Concurrent API calls to retrieve boroughs, service zones, and specific taxi zones
                val bResult = RetrofitClient.apiService.getBoroughs()
                val sResult = RetrofitClient.apiService.getServiceZones()
                val zResult = RetrofitClient.apiService.getZones()

                _boroughs.value = bResult.map { it.borough_name }
                _serviceZones.value = sResult.map { it.service_zone_name }
                _allZones.value = zResult

                // Initialize filter maps: associate each name with a default 'true' (selected) value
                selectedBoroughs.value = bResult.associate { it.borough_name to true }
                selectedServiceZones.value = sResult.associate { it.service_zone_name to true }
            } catch (e: Exception) {
                // Log exception or update UI to show an error state if necessary
            }
        }
    }

    // --- FILTER & SEARCH LOGIC ---

    /** Updates the selection state for a specific borough. **/
    fun toggleBorough(name: String, value: Boolean) {
        val current = selectedBoroughs.value.toMutableMap()
        current[name] = value
        selectedBoroughs.value = current
    }

    /** Force-enables all borough filters (logic for the 'All' checkbox). **/
    fun setAllBoroughsOn() {
        val current = selectedBoroughs.value.toMutableMap()
        current.keys.forEach { current[it] = true }
        selectedBoroughs.value = current
    }

    /** Updates the selection state for a specific service zone type. **/
    fun toggleServiceZone(name: String, value: Boolean) {
        val current = selectedServiceZones.value.toMutableMap()
        current[name] = value
        selectedServiceZones.value = current
    }

    /** Force-enables all service zone filters (logic for the 'All' checkbox). **/
    fun setAllServiceZonesOn() {
        val current = selectedServiceZones.value.toMutableMap()
        current.keys.forEach { current[it] = true }
        selectedServiceZones.value = current
    }

    /** The core data engine of the screen.
     *  Uses 'combine' to reactively re-calculate the visible list whenever
     *  the raw data, search query, or any filter checkbox changes. **/
    val filteredZones = combine(
        _allZones, searchQuery, selectedBoroughs, selectedServiceZones
    ) { zones, query, boroughs, services ->

        // 1. ADVANCED SEARCH LOGIC (Browser-style)
        // Splits the query into multiple terms by spaces, commas, or slashes.
        val searchTerms = query.trim().lowercase()
            .split(Regex("[,\\s/]+"))
            .filter { it.isNotBlank() }

        val searched = if (searchTerms.isEmpty()) {
            zones // If search is empty, proceed with all zones
        } else {
            zones.filter { zone ->
                // Matches if ANY of the entered search terms are contained in the zone_name
                searchTerms.any { term -> zone.zone_name.lowercase().contains(term) }
            }
        }

        // 2. CRITERIA FILTERING
        // Filters the search results further based on the active Borough and Service Zone checkboxes.
        searched.filter { zone ->
            val boroughActive = boroughs[zone.borough_name] ?: true
            val serviceActive = services[zone.service_zone_name] ?: true
            boroughActive && serviceActive
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
}
