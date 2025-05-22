package com.example.climaapp

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.example.climaapp.ui.theme.ClimaAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiudadesView(modifier: Modifier = Modifier) {
    var cityInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            label = { Text("Ciudad") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(context, ClimaActivity::class.java)
                intent.putExtra("ciudad", cityInput)
                context.startActivity(intent)
            },
            enabled = cityInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Buscar")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CiudadesViewPreview() {
    ClimaAppTheme {
        CiudadesView()
    }
}