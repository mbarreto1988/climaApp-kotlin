package com.example.climaapp

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.climaapp.data.tema.TemaViewModel
import com.example.climaapp.ui.theme.ClimaAppTheme
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults

// test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiudadesView(temaViewModel: TemaViewModel) {
    val isDarkMode by temaViewModel.isDarkMode.collectAsState()
    var cityInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    val ciudades = listOf(
        "Buenos Aires",
        "Córdoba",
        "Rosario",
        "Mendoza",
        "La Plata",
        "Salta",
        "San Miguel de Tucumán",
        "Mar del Plata",
        "Santa Fe",
        "San Juan",
        "Resistencia",
        "Neuquén",
        "Santiago del Estero",
        "Corrientes",
        "Bahía Blanca",
        "Paraná",
        "Posadas",
        "San Luis",
        "San Salvador de Jujuy",
        "Río Gallegos"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("Ciudad") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium
                )


                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, ClimaActivity::class.java)
                        intent.putExtra("ciudad", cityInput)
                        intent.putExtra("darkTheme", isDarkMode)
                        context.startActivity(intent)
                    },
                    enabled = cityInput.isNotBlank(),
                    modifier = Modifier
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFd0bcff),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Ubicación")
                }
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { temaViewModel.toggleDarkMode() }) {
                    val icon = if (!isDarkMode) Icons.Filled.Brightness2 else Icons.Filled.WbSunny
                    val description = if (isDarkMode) "Modo oscuro" else "Modo claro"

                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = MaterialTheme.colorScheme.onSurface,

                    )
                }


            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ciudades disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(ciudades) { ciudad ->
                            Text(
                                text = ciudad,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cityInput = ciudad }
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CiudadesPreview() {
    val fakeVM = remember { TemaViewModel() }
    val dark = fakeVM.isDarkMode.collectAsState().value
    ClimaAppTheme(darkTheme = dark) {
        CiudadesView(temaViewModel = fakeVM)
    }
}
