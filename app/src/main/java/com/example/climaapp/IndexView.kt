package com.example.climaapp

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.climaapp.ui.theme.ClimaAppTheme
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// --- MODELOS DE DATOS ---
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
    val humidity: Int
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

data class DailyForecast(
    val dt: Long,
    val temp: Temp,
    val weather: List<Weather>
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double
)

// --- CLIENTE KTOR ---
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

// --- COMPOSABLE PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexView(modifier: Modifier = Modifier) {
    var cityInput by remember { mutableStateOf("") }
    var cityInfo by remember { mutableStateOf<CityGeoInfo?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var forecast by remember { mutableStateOf<ForecastResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiKey = "ed19f75d2b20a8a8c280df206dcb079a"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = cityInput,
            onValueChange = {
                cityInput = it
                errorMessage = null
                cityInfo = null
                weather = null
                forecast = null
            },
            label = { Text("Ciudad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val geoResponse = KtorClient.getCityCoordinates("$cityInput,ar", apiKey)
                        if (geoResponse.isNotEmpty()) {
                            cityInfo = geoResponse[0]
                            val weatherResponse = KtorClient.getCurrentWeather("$cityInput,ar", apiKey)
                            weather = weatherResponse
                            forecast = KtorClient.get7DayForecast(cityInfo!!.lat, cityInfo!!.lon, apiKey)
                            errorMessage = null
                        } else {
                            errorMessage = "La ciudad no se encontró o se escribió mal."
                        }
                    } catch (e: Exception) {
                        errorMessage = "Ocurrió un error al buscar los datos."
                        Log.e("CLIMA", "Error: ${e.message}")
                    }
                }
            },
            enabled = cityInput.isNotBlank()
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        cityInfo?.let {
            Text("Ciudad: ${it.name}")
            Text("Latitud: ${it.lat}")
            Text("Longitud: ${it.lon}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        weather?.let {
            if (it.weather.isNotEmpty()) {
                val iconCode = it.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                AsyncImage(
                    model = iconUrl,
                    contentDescription = "Icono del clima",
                    modifier = Modifier.size(64.dp)
                )
                Text("Clima: ${it.weather[0].main} (${it.weather[0].description})")
            }
            Text("Temperatura: ${it.main.temp}°C (Sensación: ${it.main.feels_like}°C)")
            Text("Humedad: ${it.main.humidity}%")
            Text("Viento: ${it.wind.speed} m/s")

            Spacer(modifier = Modifier.height(16.dp))

//            Button(onClick = {
//                val shareIntent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(
//                        Intent.EXTRA_TEXT,
//                        "Pronóstico para ${it.name}: ${it.main.temp}°C, ${it.weather[0].description}"
//                    )
//                    type = "text/plain"
//                }
//                context.startActivity(Intent.createChooser(shareIntent, "Compartir clima"))
//            }) {
//                Text("Compartir")
//            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        forecast?.let { forecastResponse ->
            val filteredByDay = forecastResponse.list
                .groupBy { SimpleDateFormat("yyyy-MM-dd", Locale("es")).format(Date(it.dt * 1000)) }
                .map { it.value.first() }
                .take(5)

            filteredByDay.forEach { day ->
                val date = SimpleDateFormat("EEEE dd/MM", Locale("es")).format(Date(day.dt * 1000))
                val iconCode = day.weather.firstOrNull()?.icon ?: "01d"
                val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = iconUrl,
                        contentDescription = "Icono del clima",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "$date", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Temp: ${day.main.temp}°C",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Clima: ${day.weather.firstOrNull()?.description}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IndexViewPreview() {
    ClimaAppTheme {
        IndexView()
    }
}