package com.example.nyctaxiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

/** ViewModel for managing Payment Methods and Rate Codes.
 *  Handles API calls and provides data streams to the UI. **/
class ManagementViewModel : ViewModel() {

    // UI-observable state flows
    val paymentTypes = MutableStateFlow<List<PaymentType>>(emptyList())
    val rateCodes = MutableStateFlow<List<RateCode>>(emptyList())

    init {
        loadData()
    }

    /** Fetches fresh data from the backend. **/
    fun loadData() {
        viewModelScope.launch {
            try {
                paymentTypes.value = RetrofitClient.apiService.getPaymentTypes()
                rateCodes.value = RetrofitClient.apiService.getRateCodes()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Data loading failed: ${e.message}")
            }
        }
    }

    // --- PAYMENT TYPES LOGIC ---

    fun addPaymentType(name: String) {
        viewModelScope.launch {
            try {
                val newItem = PaymentType(id = 0, payment_type = name)
                RetrofitClient.apiService.createPaymentType(newItem)
                loadData()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Add payment failed: ${e.message}")
            }
        }
    }

    fun editPaymentType(id: Int, newName: String) {
        viewModelScope.launch {
            try {
                // Constructing object with original ID and updated name
                val editedItem = PaymentType(id = id, payment_type = newName)
                RetrofitClient.apiService.editePaymentType(id, editedItem)
                loadData()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Edit payment failed: ${e.message}")
            }
        }
    }

    fun deletePaymentType(id: Int) {
        viewModelScope.launch {
            try {
                // Handling Retrofit.Response for empty bodies (204 No Content)
                val response = RetrofitClient.apiService.deletePaymentType(id)
                if (response.isSuccessful) {
                    loadData()
                } else {
                    Log.e("API_ERROR", "Delete payment failed: Code ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Delete payment network error: ${e.message}")
            }
        }
    }

    // --- RATE CODES LOGIC ---

    fun addRateCode(code: String) {
        viewModelScope.launch {
            try {
                val newItem = RateCode(id = 0, code = code)
                RetrofitClient.apiService.createRateCode(newItem)
                loadData()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Add rate code failed: ${e.message}")
            }
        }
    }

    fun editRateCode(id: Int, newCode: String) {
        viewModelScope.launch {
            try {
                // CRITICAL FIX: Use RateCode model, not PaymentType
                val editedItem = RateCode(id = id, code = newCode)
                RetrofitClient.apiService.editeRateCode(id, editedItem)
                loadData()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Edit rate code failed: ${e.message}")
            }
        }
    }

    fun deleteRateCode(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteRateCode(id)
                if (response.isSuccessful) {
                    loadData()
                } else {
                    Log.e("API_ERROR", "Delete rate code failed: Code ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Delete rate code network error: ${e.message}")
            }
        }
    }
}
