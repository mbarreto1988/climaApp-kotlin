package com.example.climaapp

import com.example.climaapp.data.api.KtorClient
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

class APITest {
    private val apiKey = "ed19f75d2b20a8a8c280df206dcb079a"

    @Test
    fun testApi_obtenerCoordenadasCiudad() = runTest {
        val result = KtorClient.getCityCoordinates("Rosario", apiKey)
        assertNotNull("La respuesta no debe ser null", result)
        assertTrue("La lista de coordenadas no debe estar vacía", result.isNotEmpty())
    }

    @Test
    fun testApi_obtenerClimaActual() = runTest {
        val result = KtorClient.getCurrentWeather("Rosario", apiKey)
        assertNotNull("La respuesta no debe ser null", result)
        assertTrue("Debe contener temperatura", result?.main?.temp != null)
    }

    @Test
    fun testApi_Clima7Dias() = runTest {
        val lat = -32.95
        val lon = -60.66
        val result = KtorClient.get7DayForecast(lat, lon, apiKey)
        assertNotNull("La respuesta no debe ser null", result)
        assertTrue("La lista del pronóstico no debe estar vacía", result?.list?.isNotEmpty() == true)
    }
}