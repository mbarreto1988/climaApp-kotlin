package com.example.climaapp

import java.io.Serializable

data class DatoClima(
    val dt: String,
    val temp: Double
) : Serializable