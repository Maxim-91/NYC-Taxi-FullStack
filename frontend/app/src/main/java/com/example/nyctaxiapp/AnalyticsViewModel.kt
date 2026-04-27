package com.example.nyctaxiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel : ViewModel() {
    private val _chartData = MutableStateFlow<List<AvgAmountItem>>(emptyList())
    val chartData: StateFlow<List<AvgAmountItem>> = _chartData

    private val _selectedDt = MutableStateFlow<Long?>(null)
    val selectedDt: StateFlow<Long?> = _selectedDt

    private val _selectedStep = MutableStateFlow("year")
    val selectedStep: StateFlow<String> = _selectedStep

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun updateDt(millis: Long) {
        _selectedDt.value = millis
        performRequest()
    }

    fun updateStep(step: String) {
        _selectedStep.value = step
        if (_selectedDt.value != null) performRequest()
    }

    fun getFormattedDate(): String {
        return _selectedDt.value?.let {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }

    private fun performRequest() {
        val dt = _selectedDt.value ?: return
        val step = _selectedStep.value
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = RetrofitClient.apiService.getAvgAmount(dt.toString(), step)
                _chartData.value = result.sortedBy { it.timeLabel }
            } catch (e: Exception) {
                _chartData.value = emptyList()
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
