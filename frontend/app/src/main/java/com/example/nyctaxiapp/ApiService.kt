package com.example.nyctaxiapp

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class AvgAmountItem(
    val avg_amount: Float,
    @SerializedName(value = "pu_hour", alternate = ["pu_day", "pu_month"])
    val timeLabel: Int
)

interface ApiService {
    @GET("api/v1/yellow_trips/{dt}/{step}/avg_amount")
    suspend fun getAvgAmount(
        @Path("dt") dt: String,
        @Path("step") step: String
    ): List<AvgAmountItem>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/" // Android emulator

    // Створюємо клієнт, який буде чекати довгі запити
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES) // Час на з'єднання
        .readTimeout(5, TimeUnit.MINUTES)    // Час на отримання даних
        .writeTimeout(5, TimeUnit.MINUTES)   // Час на відправку
        .build()
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ЦЕЙ РЯДОК ОБОВ'ЯЗКОВИЙ
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
