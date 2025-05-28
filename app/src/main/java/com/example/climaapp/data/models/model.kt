package com.example.climaapp.data.models

import java.io.Serializable

data class CityGeoInfo(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)

data class DatoClima(
    val dt: String,
    val temp: Double
) : Serializable

