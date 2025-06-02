package com.example.climaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climaapp.data.tema.TemaViewModel
import com.example.climaapp.ui.theme.ClimaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val temaViewModel: TemaViewModel = viewModel()
            ClimaAppTheme(darkTheme = temaViewModel.isDarkMode.collectAsState().value) {
                CiudadesView(temaViewModel = temaViewModel)
            }
        }
    }
}
