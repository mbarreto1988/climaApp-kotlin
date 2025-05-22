package com.example.climaapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.climaapp.data.api.KtorClient
import com.example.climaapp.data.models.CityGeoInfo
import com.example.climaapp.data.models.ForecastResponse
import com.example.climaapp.data.models.WeatherResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClimaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CLIMA", "Estoy en el Clima")
        val ciudad = intent.getStringExtra("ciudad") ?: ""

        setContent {
            ClimaScreen(ciudad = ciudad, onBack = { finish() })
        }
    }
}

@Composable
fun ClimaScreen(ciudad: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var cityInfo by remember { mutableStateOf<CityGeoInfo?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var forecast by remember { mutableStateOf<ForecastResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val apiKey = "ed19f75d2b20a8a8c280df206dcb079a"

    LaunchedEffect(ciudad) {
        scope.launch {
            try {
                val geo = KtorClient.getCityCoordinates("$ciudad,ar", apiKey)
                if (geo.isNotEmpty()) {
                    cityInfo = geo[0]
                    weather = KtorClient.getCurrentWeather("$ciudad,ar", apiKey)
                    forecast = KtorClient.get7DayForecast(geo[0].lat, geo[0].lon, apiKey)
                } else {
                    error = "Ciudad no encontrada."
                }
            } catch (e: Exception) {
                error = "Error al cargar datos."
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clima en ${ciudad.uppercase()}", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            return@Column
        }

        weather?.let {
            val icon = it.weather[0].icon
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                contentDescription = "Icono",
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Clima: ${it.weather[0].main} (${it.weather[0].description})")
            Text("Temp: ${it.main.temp}°C (Sensación: ${it.main.feels_like}°C)")
            Text("Humedad: ${it.main.humidity}%")
            Text("Viento: ${it.wind.speed} m/s")
        }

        Spacer(Modifier.height(8.dp))
        Text("Pronóstico 5 días", style = MaterialTheme.typography.titleMedium)

        forecast?.let { forecastData ->
            val porDia = forecastData.list
                .groupBy { SimpleDateFormat("yyyy-MM-dd", Locale("es")).format(Date(it.dt * 1000)) }
                .map { it.value.first() }
                .take(5)

            porDia.forEach { dia ->
                val fecha = SimpleDateFormat("EEEE dd/MM", Locale("es")).format(Date(dia.dt * 1000))
                val icon = dia.weather.firstOrNull()?.icon ?: "01d"
                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = fecha, style = MaterialTheme.typography.titleMedium)
                            Text("Temp: ${dia.main.temp}°C")
                            Text("Clima: ${dia.weather.firstOrNull()?.description}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}