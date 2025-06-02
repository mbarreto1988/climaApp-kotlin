package com.example.climaapp

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.climaapp.data.tema.TemaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test

class FakeTemaViewModel : TemaViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    override val isDarkMode: StateFlow<Boolean> get() = _isDarkMode

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}

class CiudadesViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCityInput_and_EnableButton() {
        val fakeViewModel = FakeTemaViewModel()
        composeTestRule.setContent {
            CiudadesView(temaViewModel = fakeViewModel)
        }

        composeTestRule
            .onNode(hasText("Ciudad") and hasSetTextAction())
            .performTextInput("Rosario")

        composeTestRule
            .onNode(
                hasClickAction() and hasAnyDescendant(hasContentDescription("Ubicación")),
                useUnmergedTree = true
            )
            .assertIsEnabled()
    }

    @Test
    fun testButtonDisabledWhenInputEmpty() {
        val fakeViewModel = FakeTemaViewModel()
        composeTestRule.setContent {
            CiudadesView(temaViewModel = fakeViewModel)
        }

        composeTestRule
            .onNode(
                hasClickAction() and hasAnyDescendant(hasContentDescription("Ubicación")),
                useUnmergedTree = true
            )
            .assertIsNotEnabled()
    }

    @Test
    fun testClickCityFromList_updatesTextField() {
        val fakeViewModel = FakeTemaViewModel()
        composeTestRule.setContent {
            CiudadesView(temaViewModel = fakeViewModel)
        }

        composeTestRule.onNode(hasText("Rosario")).performClick()

        composeTestRule
            .onNode(hasText("Ciudad") and hasSetTextAction())
            .assert(hasText("Rosario"))
    }

    @Test
    fun testToggleDarkModeButton_clickable() {
        val fakeViewModel = FakeTemaViewModel()
        composeTestRule.setContent {
            CiudadesView(temaViewModel = fakeViewModel)
        }

        composeTestRule
            .onNode(hasClickAction() and hasAnyDescendant(hasContentDescription("Modo claro")), useUnmergedTree = true)
            .performClick()
    }
}
