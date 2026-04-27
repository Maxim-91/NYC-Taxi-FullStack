package com.example.nyctaxiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/** ViewModel responsible for managing the state and business logic of the Analytics screen.
 *  It handles data fetching from the API and provides observable state to the UI. **/
class AnalyticsViewModel : ViewModel() {

    // --- Observable States (Backing Properties) ---

    // Holds the list of data items used to populate the chart
    private val _chartData = MutableStateFlow<List<AvgAmountItem>>(emptyList())
    val chartData: StateFlow<List<AvgAmountItem>> = _chartData

    // Stores the user-selected date in milliseconds
    private val _selectedDt = MutableStateFlow<Long?>(null)
    val selectedDt: StateFlow<Long?> = _selectedDt

    // Stores the current granularity step: "year", "month", or "day"
    private val _selectedStep = MutableStateFlow("year")
    val selectedStep: StateFlow<String> = _selectedStep

    // Indicates whether a network request is currently in progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Holds any error messages resulting from failed network requests
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // --- Public API / Actions ---

    /** Updates the selected date and triggers a new data request.
     *  @param millis The date selected from the DatePicker in milliseconds. **/
    fun updateDt(millis: Long) {
        _selectedDt.value = millis
        performRequest()
    }

    /** Updates the granularity step and triggers a data request if a date is already selected.
     *  @param step The new resolution ("year", "month", or "day"). **/
    fun updateStep(step: String) {
        _selectedStep.value = step
        if (_selectedDt.value != null) performRequest()
    }

    /** Formats the selected timestamp into a human-readable string for the UI.
     * @return Formatted date string (dd.MM.yyyy) or empty string if no date is selected. **/
    fun getFormattedDate(): String {
        return _selectedDt.value?.let {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }

    // --- Private Logic ---

    /** Executes the network request to fetch taxi analytics data.
     *  Uses viewModelScope to ensure the coroutine is cancelled when the ViewModel is cleared. **/
    private fun performRequest() {
        val dt = _selectedDt.value ?: return
        val step = _selectedStep.value

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Reset error state before starting request

            try {
                // Call the Retrofit API service
                val result = RetrofitClient.apiService.getAvgAmount(dt.toString(), step)

                // Sort data by time label to ensure the chart line draws chronologically
                _chartData.value = result.sortedBy { it.timeLabel }
            } catch (e: Exception) {
                // Clear chart and capture error message on failure (e.g., timeout or no connection)
                _chartData.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Unknown error occurred"
            } finally {
                // Hide loading indicator regardless of success or failure
                _isLoading.value = false
            }
        }
    }
}
