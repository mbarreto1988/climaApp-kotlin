package com.example.climaapp.data.tema

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class TemaViewModel : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    open val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun processIntent(intent: TemaIntent) {
        when (intent) {
            is TemaIntent.ToggleTheme -> toggleDarkMode()
        }
    }

    private fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}
