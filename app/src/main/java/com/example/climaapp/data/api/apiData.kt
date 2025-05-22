package com.example.climaapp.data.api

import android.util.Log
import com.example.climaapp.data.models.CityGeoInfo
import com.example.climaapp.data.models.ForecastResponse
import com.example.climaapp.data.models.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.gson.gson

object KtorClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getCityCoordinates(cityName: String, apiKey: String): List<CityGeoInfo> {
        return try {
            client.get("https://api.openweathermap.org/geo/1.0/direct") {
                parameter("q", cityName)
                parameter("limit", 5)
                parameter("appid", apiKey)
            }.body()
        } catch (e: Exception) {
            Log.e("Ktor", "Error geo: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCurrentWeather(cityName: String, apiKey: String): WeatherResponse? {
        return try {
            client.get("https://api.openweathermap.org/data/2.5/weather") {
                parameter("q", cityName)
                parameter("appid", apiKey)
                parameter("units", "metric")
                parameter("lang", "es")
            }.body()
        } catch (e: Exception) {
            Log.e("Ktor", "Error clima: ${e.message}")
            null
        }
    }

    suspend fun get7DayForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse? {
        return try {
            client.get("https://api.openweathermap.org/data/2.5/forecast") {
                parameter("lat", lat)
                parameter("lon", lon)
                parameter("units", "metric")
                parameter("lang", "es")
                parameter("appid", apiKey)
            }.body()
        } catch (e: Exception) {
            Log.e("Ktor", "Error pronóstico: ${e.message}")
            null
        }
    }
}