package com.example.climaapp

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climaapp.ui.theme.ClimaAppTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


data class CityGeoInfo(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

interface GeoApiService {
    @GET("geo/1.0/direct")
    suspend fun getCityCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<CityGeoInfo>
}

object RetrofitClient {
    val api: GeoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoApiService::class.java)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexView(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Buenos Aires", "Córdoba", "Rosario")
    var selectedCity by remember { mutableStateOf(cities[0]) }
    var cityInfo by remember { mutableStateOf<CityGeoInfo?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedCity,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ciudad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            selectedCity = city
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    val response = RetrofitClient.api.getCityCoordinates(
                        cityName = "$selectedCity,ar",
                        apiKey = "ed19f75d2b20a8a8c280df206dcb079a"
                    )
                    if (response.isNotEmpty()) {
                        cityInfo = response[0]
                        Log.d("CLIMA", "Ciudad: ${cityInfo?.name}, lat: ${cityInfo?.lat}, lon: ${cityInfo?.lon}")
                    } else {
                        Log.d("CLIMA", "No se encontraron resultados para $selectedCity")
                    }
                } catch (e: Exception) {
                    Log.e("CLIMA", "Error: ${e.message}")
                }
            }
        }) {
            Text("Seleccionar")
        }

        cityInfo?.let {
            Text("Ciudad seleccionada: ${it.name}")
            Text("Latitud: ${it.lat}")
            Text("Longitud: ${it.lon}")
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
