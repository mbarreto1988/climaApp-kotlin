package com.example.climaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.climaapp.data.tema.TemaViewModel
import com.example.climaapp.ui.theme.ClimaAppTheme

class MainActivity : ComponentActivity() {

    private val temaViewModel: TemaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = temaViewModel.isDarkMode.collectAsState().value

            ClimaAppTheme(
                darkTheme = isDark,
                dynamicColor = true //
            ) {
                CiudadesView(
                    modifier = Modifier,
                    temaViewModel = temaViewModel
                )
            }
        }
    }
}


