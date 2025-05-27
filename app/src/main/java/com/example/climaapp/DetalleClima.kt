package com.example.climaapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.climaapp.ui.GraficosClima
import com.google.gson.Gson
import kotlin.math.roundToInt


class DetalleClimaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fecha = intent.getStringExtra("fecha")
        val temp = intent.getStringExtra("temp")
        val descripcion = intent.getStringExtra("descripcion")
        val icon = intent.getStringExtra("icon")
        val datosDia = intent.getSerializableExtra("datosDia") as ArrayList<DatoClima>
        val gson = Gson()
        Log.d("datosDiaG", gson.toJson(datosDia))


        setContent {
            Column {
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
                        Text("Fecha: $fecha")
                        Text("Temp: $temp °C")
                        Text("Descripción: $descripcion")
                    }
                }

                val temperaturas = datosDia.map { it.temp.roundToInt().toFloat()}
                val horas = listOf(0f, 3f, 6f, 9f, 12f, 15f, 18f)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    GraficosClima(temperaturasPorHora = temperaturas, horas = horas)
                }
            }
        }
    }
}