package com.example.climaapp.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.climaapp.ClimaScreen
import com.example.climaapp.ui.theme.ClimaAppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import androidx.compose.ui.graphics.Color
/*import com.patrykandpatrick.vico.core.AxisDefaults
import com.patrykandpatrick.vico.compose.axis.Axis
import com.patrykandpatrick.vico.compose.axis.AxisLabel
import com.patrykandpatrick.vico.compose.axis.axisLabel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
*/

class GraficosClimaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val temperaturasPorHora = intent.getFloatArrayExtra("temperaturasPorHora")?.toList() ?: emptyList()
        val horas = intent.getFloatArrayExtra("horas")?.toList() ?: emptyList()
        val isDarkTheme = intent.getBooleanExtra("isDarkTheme", false)

        setContent {
            ClimaAppTheme(darkTheme = isDarkTheme) {
                GraficosClima(
                    temperaturasPorHora = temperaturasPorHora,
                    horas=horas,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun GraficosClima(
    temperaturasPorHora: List<Float>,
    horas: List<Float>,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val context = LocalContext.current
    val activity = context as? Activity


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

    /*
    val bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = horaFormatter,
        label = AxisDefaults.Labels(
            textColor = labelColor
        ),
        line = AxisDefaults.Line(
            color = lineColor
        ),
        guideline = AxisDefaults.Guideline(
            color = guidelineColor
        )
    )

    val verticalAxis = Axis.vertical(
        axisLabel = AxisLabel(
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Red,
                fontSize = 12.sp
            )
        )
    )
    */

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f).background(Color.White)) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = horaFormatter
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { activity?.finish() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

