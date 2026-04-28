package com.example.nyctaxiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LocationsViewModel : ViewModel() {
    // Дані з API
    private val _allZones = MutableStateFlow<List<Zone>>(emptyList())
    private val _boroughs = MutableStateFlow<List<String>>(emptyList())
    private val _serviceZones = MutableStateFlow<List<String>>(emptyList())

    // Стан пошуку та фільтрів
    val searchQuery = MutableStateFlow("")
    val selectedBoroughs = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val selectedServiceZones = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    // Стан "All" чекбоксів
    val isAllBoroughsOn = selectedBoroughs.map { map -> map.values.all { it } }
    val isAllServiceZonesOn = selectedServiceZones.map { map -> map.values.all { it } }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // В реальному проекті тут виклики RetrofitClient.apiService
                val bResult = RetrofitClient.apiService.getBoroughs()
                val sResult = RetrofitClient.apiService.getServiceZones()
                val zResult = RetrofitClient.apiService.getZones()

                _boroughs.value = bResult.map { it.borough_name }
                _serviceZones.value = sResult.map { it.service_zone_name }
                _allZones.value = zResult

                // Ініціалізація чекбоксів значенням ON
                selectedBoroughs.value = bResult.associate { it.borough_name to true }
                selectedServiceZones.value = sResult.associate { it.service_zone_name to true }
            } catch (e: Exception) { /* Handle error */ }
        }
    }

    // Логіка перемикання конкретного району
    fun toggleBorough(name: String, value: Boolean) {
        val current = selectedBoroughs.value.toMutableMap()
        current[name] = value
        selectedBoroughs.value = current
    }

    // Логіка "All" для районів
    fun setAllBoroughsOn() {
        val current = selectedBoroughs.value.toMutableMap()
        current.keys.forEach { current[it] = true }
        selectedBoroughs.value = current
    }

    fun toggleServiceZone(name: String, value: Boolean) {
        val current = selectedServiceZones.value.toMutableMap()
        current[name] = value
        selectedServiceZones.value = current
    }

    fun setAllServiceZonesOn() {
        val current = selectedServiceZones.value.toMutableMap()
        current.keys.forEach { current[it] = true }
        selectedServiceZones.value = current
    }

    // Головний потік відфільтрованих даних
    val filteredZones = combine(
        _allZones, searchQuery, selectedBoroughs, selectedServiceZones
    ) { zones, query, boroughs, services ->

        // 1. Пошук (Логіка браузера)
        val searchTerms = query.trim().lowercase().split(Regex("[,\\s/]+")).filter { it.isNotBlank() }

        val searched = if (searchTerms.isEmpty()) zones else {
            zones.filter { zone ->
                searchTerms.any { term -> zone.zone_name.lowercase().contains(term) }
            }
        }

        // 2. Фільтрація по Borough та Service Zone
        searched.filter { zone ->
            (boroughs[zone.borough_name] ?: true) && (services[zone.service_zone_name] ?: true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
