package com.example.climaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.climaapp.data.api.KtorClient
import com.example.climaapp.data.models.CityGeoInfo
import com.example.climaapp.data.models.ForecastResponse
import com.example.climaapp.data.models.WeatherResponse
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.climaapp.ui.theme.ClimaAppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import com.example.climaapp.data.models.DatoClima
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

fun obtenerHora(s: String): String {
    val formatoEntrada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val formatoSalida = SimpleDateFormat("HH:mm", Locale.getDefault())

    val fecha = formatoEntrada.parse(s)
    return formatoSalida.format(fecha!!)
}

fun convertirTimestampAHoraArgentinaCompat(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "AR"))
    sdf.timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
    val diaHora= sdf.format(date)
    return obtenerHora(diaHora)
}

class ClimaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState = savedInstanceState)
        val ciudad = intent.getStringExtra("ciudad") ?: ""
        val isDark = intent.getBooleanExtra("darkTheme", false)
        setContent {
            ClimaAppTheme(darkTheme = isDark) {
                ClimaScreen(ciudad = ciudad, isDark = isDark, onBack = { finish() })
            }
        }
    }
}

@Composable
fun ClimaScreen(ciudad: String, isDark: Boolean, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var cityInfo by remember { mutableStateOf<CityGeoInfo?>(null) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var forecast by remember { mutableStateOf<ForecastResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val apiKey = "ed19f75d2b20a8a8c280df206dcb079a"
    val context = LocalContext.current
    val isLoading = weather == null && error == null


    LaunchedEffect(ciudad) {
        scope.launch {
            try {
                val geo = KtorClient.getCityCoordinates("$ciudad,ar", apiKey)
                if (geo.isNotEmpty()) {
                    cityInfo = geo[0]
                    weather = KtorClient.getCurrentWeather("$ciudad,ar", apiKey)
                    forecast = KtorClient.get7DayForecast(geo[0].lat, geo[0].lon, apiKey)
                } else {
                    error =
                        "No pudimos encontrar la ciudad ingresada. Verifica el nombre e intenta de nuevo."
                }
            } catch (e: Exception) {
                error =
                    "No pudimos cargar el clima. Revisa tu conexión a internet o intenta más tarde."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Clima en ${ciudad.uppercase()}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Volver a buscar ciudad")
                }
            }
            return
        }

        AnimatedVisibility(visible = isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Cargando datos del clima...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        weather?.let {
            val icon = it.weather[0].icon
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                contentDescription = "Icono",
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Clima: ${it.weather[0].main} (${it.weather[0].description})",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Temp: ${it.main.temp}°C (Sensación: ${it.main.feels_like}°C)",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Humedad: ${it.main.humidity}%",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Viento: ${it.wind.speed} m/s",
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            val textoPronostico = """
              Pronóstico para ${ciudad.uppercase()}:
              - Estado: ${it.weather[0].main} (${it.weather[0].description})
              - Temperatura: ${it.main.temp}°C (Sensación: ${it.main.feels_like}°C)
              - Humedad: ${it.main.humidity}%
              - Viento: ${it.wind.speed} m/s
              """.trimIndent()


            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Pronóstico del clima")
                        putExtra(Intent.EXTRA_TEXT, textoPronostico)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir con"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )

            ) {
                Icon(Icons.Default.Share, contentDescription = "Compartir")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compartir pronóstico")
            }
        }


        Spacer(Modifier.height(8.dp))
        Text(
            "Pronóstico 5 días", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        forecast?.let { forecastData ->
            val porDia = forecastData.list
                .groupBy { SimpleDateFormat("yyyy-MM-dd", Locale("es")).format(Date(it.dt * 1000)) }
                .entries
                .take(5)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    porDia.forEach { (fechaKey, listaDelDia) ->
                        val diaResumen = listaDelDia.first()
                        val fecha = SimpleDateFormat(
                            "EEEE dd/MM",
                            Locale("es")
                        ).format(Date(diaResumen.dt * 1000))
                        val icon = diaResumen.weather.firstOrNull()?.icon ?: "01d"
                        val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                        val datosDia = listaDelDia.map {
                            DatoClima(
                                convertirTimestampAHoraArgentinaCompat(it.dt),
                                it.main.temp
                            )
                        } as ArrayList<DatoClima>

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    val intent = Intent(context, DetalleClimaActivity::class.java)

                                    intent.putExtra("fecha", fecha)
                                    intent.putExtra("temp", diaResumen.main.temp.toString())
                                    intent.putExtra("isDarkTheme", isDark)
                                    intent.putExtra(
                                        "descripcion",
                                        diaResumen.weather.firstOrNull()?.description ?: "Sin datos"
                                    )
                                    intent.putExtra("icon", icon)
                                    intent.putExtra("datosDia", datosDia)

                                    context.startActivity(intent)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFCCF2E8))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                AsyncImage(
                                    model = iconUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = fecha, style = MaterialTheme.typography.titleMedium)
                                    Text("Temp: ${diaResumen.main.temp}°C")
                                    Text("Clima: ${diaResumen.weather.firstOrNull()?.description}")

                                }
                            }
                        }
                    }
                }


            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver")
        }
    }
}