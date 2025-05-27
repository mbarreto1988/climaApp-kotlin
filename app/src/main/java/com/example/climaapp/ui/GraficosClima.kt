package com.example.climaapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import kotlinx.coroutines.runBlocking
import com.patrykandpatrick.vico.core.cartesian.axis.Axis



@Composable
fun GraficosClima(
    temperaturasPorHora: List<Float>,
    horas: List<Float>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(temperaturasPorHora, horas) {
        modelProducer.runTransaction {
            lineSeries {
                series(horas, temperaturasPorHora)
            }
        }
    }

    val horaFormatter = remember {
        object : CartesianValueFormatter {
            override fun format(
                context: CartesianMeasuringContext,
                value: Double,
                verticalAxisPosition: Axis.Position.Vertical?
            ): CharSequence {
                return "${value.toInt()}h"
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = horaFormatter
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}


@Composable
@Preview
private fun Preview() {
    val temperaturas = listOf(22f, 24f, 20f, 25f, 28f, 30f, 26f, 22f, 20f)
    val horas = listOf(0f, 3f, 6f, 9f, 12f, 15f, 18f, 21f, 24f)
    PreviewBox {
        GraficosClima(
            temperaturasPorHora = temperaturas,
            horas = horas
        )
    }
}


