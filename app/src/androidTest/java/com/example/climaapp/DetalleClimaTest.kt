package com.example.climaapp

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.climaapp.data.models.DatoClima
import com.google.gson.Gson
import org.junit.Rule
import org.junit.Test
//
class DetalleClimaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun muestraDatosRecibidosCorrectamente() {
        // Datos simulados para test
        val datosDia = arrayListOf(
            DatoClima("00:00", 15.0),
            DatoClima("03:00", 13.5),
            DatoClima("06:00", 12.0)
        )

        val intent = Intent(composeTestRule.activity, DetalleClimaActivity::class.java).apply {
            putExtra("fecha", "jueves 06/06")
            putExtra("temp", "15.0")
            putExtra("descripcion", "nubes")
            putExtra("icon", "01d")
            putExtra("datosDia", datosDia)
            putExtra("isDarkTheme", false)
        }

        composeTestRule.activityRule.scenario.onActivity {
            it.startActivity(intent)
        }

        composeTestRule.onNodeWithText("Fecha: jueves 06/06").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temp: 15.0 °C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción: nubes").assertIsDisplayed()
    }
}
