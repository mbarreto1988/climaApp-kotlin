package com.example.climaapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.climaapp.data.models.DatoClima
import com.example.climaapp.ui.GraficosClima
import com.example.climaapp.ui.theme.ClimaAppTheme
import com.google.gson.Gson


class DetalleClimaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fecha = intent.getStringExtra("fecha")
        val temp = intent.getStringExtra("temp")
        val descripcion = intent.getStringExtra("descripcion")
        val icon = intent.getStringExtra("icon")
        val datosDia = intent.getSerializableExtra("datosDia") as ArrayList<DatoClima>
        val gson = Gson()
        val isDarkTheme = intent.getBooleanExtra("isDarkTheme", false)
        Log.d("datosDiaG", gson.toJson(datosDia))
        println(isDarkTheme)
        setContent {
            ClimaAppTheme(darkTheme = isDarkTheme) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Fecha: $fecha",color = MaterialTheme.colorScheme.onSurface)
                            Text("Temp: $temp °C",color = MaterialTheme.colorScheme.onSurface)
                            Text("Descripción: $descripcion",color = MaterialTheme.colorScheme.onSurface)

                        }
                    }

                    val temperaturas = datosDia.map { it.temp.toFloat() }
                    val totalHoras = List(8) { it * 3f }
                    //Muestra solo las temperaturas restantes del dia actual porque la api solo devuelve las temperaturas restantes dependiendo la hora del dia que se consulte.
                    val horas = totalHoras.takeLast(temperaturas.size)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        GraficosClima(
                            temperaturasPorHora = temperaturas,
                            horas = horas,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}