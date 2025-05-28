package com.example.climaapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier.Companion.any
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.climaapp.data.api.KtorClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
