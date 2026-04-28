package com.example.nyctaxiapp

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/** Data model representing the average trip amount for a specific time period. **/
data class AvgAmountItem(
    val avg_amount: Float,
    /** Use @SerializedName with alternates to handle different JSON keys
     *  from the server (pu_hour, pu_day, or pu_month) based on the requested step. **/
    @SerializedName(value = "pu_hour", alternate = ["pu_day", "pu_month"])
    val timeLabel: Int
)

/** Retrofit interface defining the API endpoints. **/
interface ApiService {
    /** Fetches average taxi trip amounts.
     *  @param dt The timestamp (milliseconds) selected by the user.
     *  @param step The granularity of the data: "year", "month", or "day". **/
    @GET("api/v1/yellow_trips/{dt}/{step}/avg_amount")
    suspend fun getAvgAmount(
        @Path("dt") dt: String,
        @Path("step") step: String
    ): List<AvgAmountItem>

    /** Fetches boroughs, service zones, and zones. **/
    @GET("api/v1/boroughs")
    suspend fun getBoroughs(): List<Borough>

    @GET("api/v1/zones")
    suspend fun getZones(): List<Zone>

    @GET("api/v1/service_zones")
    suspend fun getServiceZones(): List<ServiceZone>
}

/** Singleton object to manage the Retrofit instance and network configuration. **/
object RetrofitClient {
    /** Base URL for the API endpoint "10.0.2.2" is a special IP address that points to the host machine's
     *  localhost from the Android Emulator. **/
    private const val BASE_URL = "http://10.0.2.2:5000/" // Android emulator localhost

    /** Configure a custom OkHttpClient to handle long-running server requests.
     *  Timeouts are increased to 5 minutes to accommodate slow backend processing. **/
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES) // Time allowed to establish connection
        .readTimeout(5, TimeUnit.MINUTES)    // Time allowed to wait for data packets
        .writeTimeout(5, TimeUnit.MINUTES)   // Time allowed to send data packets
        .build()

    /** Retrofit instance for the API. Lazy initialization of the ApiService.
     *  Ensures the Retrofit instance is only created when first accessed. **/
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach the custom client with extended timeouts
            .addConverterFactory(GsonConverterFactory.create()) // Use GSON for JSON parsing
            .build()
            .create(ApiService::class.java)
    }
}

/** Models for Locations screen. **/
data class Borough(val id: Int, val borough_name: String)
data class ServiceZone(val id: Int, val service_zone_name: String)
data class Zone(
    val LocationID: Int,
    val borough_name: String,
    val service_zone_name: String,
    val zone_name: String
)




