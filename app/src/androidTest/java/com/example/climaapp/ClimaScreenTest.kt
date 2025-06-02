package com.example.climaapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test


@Suppress("MISSING_DEPENDENCY_CLASS_IN_EXPRESSION_TYPE")
class ClimaScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun climaScreen_muestraIndicadorCargaAlInicio() {
        composeTestRule.setContent {
            ClimaScreen(ciudad = "Buenos Aires", isDark = false, onBack = {})
        }

        composeTestRule
            .onNodeWithText("Cargando datos del clima...")
            .assertIsDisplayed()
    }

    @Test
    fun climaScreen_clicEnBotonVolverLlamaCallback() {
        var volvio = false
        composeTestRule.setContent {
            ClimaScreen(ciudad = "Buenos Aires", isDark = false, onBack = { volvio = true })
        }

        composeTestRule
            .onNodeWithText("Volver")
            .performClick()

        assert(volvio)
    }
}
