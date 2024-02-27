package hr.foi.tbp.keepfit.page

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import hr.foi.tbp.keepfit.model.request.HealthCreateRequest
import hr.foi.tbp.keepfit.model.request.HealthPatchRequest
import hr.foi.tbp.keepfit.model.response.HealthGraphValue
import hr.foi.tbp.keepfit.model.response.HealthIndicators
import hr.foi.tbp.keepfit.model.response.HealthResponse
import hr.foi.tbp.keepfit.viewmodel.HealthViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun HealthPage(
    healthViewModel: HealthViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDialogCreate by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(3, 6, 21))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                HealthActionButton("Add today's health") { showDialogCreate = true }
            }
            item {
                HealthDataChart(healthViewModel)
            }
        }
    }

    if (showDialogCreate) {
        CreateHealthDialog(
            onCreate = {
                healthViewModel.tryPostHealth(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            healthViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            healthViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        showDialogCreate = false
                    }
                )
            },
            onDismiss = { showDialogCreate = false }
        )
    }
}

@Composable
fun HealthDataChart(
    healthViewModel: HealthViewModel,
) {
    val context = LocalContext.current
    val healthDataGraph by healthViewModel.healthResponseGraph.observeAsState(null)
    var selectedMetric by remember { mutableStateOf("blood_glucose") }
    var selectedDataList by remember { mutableStateOf<List<List<HealthGraphValue>>>(emptyList()) }

    fun showFailedToast() {
        Toast.makeText(
            context,
            healthViewModel.apiMessage.value.toString(),
            Toast.LENGTH_LONG
        ).show()
    }

    LaunchedEffect(selectedMetric) {
        when (selectedMetric) {
            "blood_glucose" -> {
                healthViewModel.tryGetHealthGraphData("blood_glucose",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.bloodGlucose ?: emptyList()
                        selectedDataList = listOf(newDataList)
                    })
            }

            "heart_rate" -> {
                healthViewModel.tryGetHealthGraphData("heart_rate",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.heartRate ?: emptyList()
                        selectedDataList = listOf(newDataList)
                    })
            }

            "blood_pressure" -> {
                healthViewModel.tryGetHealthGraphData("blood_pressure",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.bloodPressure ?: emptyList()

                        val firstElements = mutableListOf<HealthGraphValue>()
                        val secondElements = mutableListOf<HealthGraphValue>()

                        newDataList.forEach {
                            val parts = it.value.split("/")
                            if (parts.size == 2) {
                                val firstNumber = parts[0].toFloatOrNull()
                                val secondNumber = parts[1].toFloatOrNull()

                                if (firstNumber != null && secondNumber != null) {
                                    firstElements.add(HealthGraphValue(it.date, firstNumber))
                                    secondElements.add(HealthGraphValue(it.date, secondNumber))
                                }
                            }
                        }

                        selectedDataList = listOf(firstElements, secondElements)
                    })
            }

            "respiration_rate" -> {
                healthViewModel.tryGetHealthGraphData("respiration_rate",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.respirationRate ?: emptyList()
                        selectedDataList = listOf(newDataList)
                    })
            }

            "body_temperature" -> {
                healthViewModel.tryGetHealthGraphData("body_temperature",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.bodyTemperature ?: emptyList()
                        selectedDataList = listOf(newDataList)
                    })
            }

            "current_weight" -> {
                healthViewModel.tryGetHealthGraphData("current_weight",
                    onFailed = { showFailedToast() },
                    onSucceed = {
                        val newDataList = healthDataGraph?.currentWeight ?: emptyList()
                        selectedDataList = listOf(newDataList)
                    })
            }
        }
    }

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Text(
            text = "Indicators visualization - $selectedMetric",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {

                Graph(*selectedDataList.toTypedArray())

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    border = BorderStroke(3.dp, Color(3, 6, 21)),
                    color = Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { selectedMetric = "blood_glucose" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("BG") }
                            Button(
                                onClick = { selectedMetric = "heart_rate" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("HR") }
                            Button(
                                onClick = { selectedMetric = "blood_pressure" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("BP") }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { selectedMetric = "respiration_rate" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("RR") }
                            Button(
                                onClick = { selectedMetric = "body_temperature" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("BT") }
                            Button(
                                onClick = { selectedMetric = "current_weight" },
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(3, 6, 33),
                                    contentColor = Color.White
                                ),
                            ) { Text("CW") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Graph(vararg selectedDataLists: List<HealthGraphValue>) {
    val lines = mutableListOf<Line>()

    selectedDataLists.forEachIndexed { _, selectedDataList ->
        val pointsData: List<Point> = if (selectedDataList.isEmpty()) {
            listOf(
                Point(0f, 0f),
                Point(1f, 0f),
                Point(2f, 0f),
                Point(3f, 0f),
                Point(4f, 0f)
            )
        } else {
            selectedDataList.mapIndexed { index, element ->
                Point(
                    index.toFloat(),
                    element.value.toFloat()
                )
            }
        }

        val line = Line(
            dataPoints = pointsData,
            LineStyle(color = Color(3, 6, 21)),
            IntersectionPoint(),
            SelectionHighlightPoint(),
            ShadowUnderLine(),
            SelectionHighlightPopUp()
        )
        lines.add(line)
    }

    val xAxisData = AxisData.Builder()
        .labelAndAxisLinePadding(50.dp)
        .axisStepSize(100.dp)
        .axisLineColor(Color.White)
        .axisLabelColor(Color.Black)
        .backgroundColor(Color.White)
        .steps(lines.firstOrNull()?.dataPoints?.size?.minus(1) ?: 0)
        .labelData { index ->
            val selectedDataList = selectedDataLists.firstOrNull()
            selectedDataList?.let {
                if (index < it.size) {
                    val dateTime = LocalDateTime.parse(
                        it[index].date,
                        DateTimeFormatter.ISO_DATE_TIME
                    ).plusDays(1)

                    dateTime.toLocalDate().toString()
                } else {
                    index.toString()
                }
            } ?: index.toString()
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(lines = lines),
        xAxisData = xAxisData,
        backgroundColor = Color.White
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}

@Composable
fun CreateHealthDialog(
    onCreate: (health: HealthCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var bloodGlucose by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var respirationRate by remember { mutableStateOf("") }
    var bodyTemperature by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Add health indicators") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                LazyColumn {
                    item {
                        TextField(value = bloodGlucose, onValueChange = {
                            bloodGlucose = it
                        }, label = { Text("Blood glucose") })
                    }

                    item {
                        TextField(value = bloodPressure, onValueChange = {
                            bloodPressure = it
                        }, label = { Text("Blood pressure") })
                    }

                    item {
                        TextField(value = heartRate, onValueChange = {
                            heartRate = it
                        }, label = { Text("Heart rate") })
                    }

                    item {
                        TextField(value = respirationRate, onValueChange = {
                            respirationRate = it
                        }, label = { Text("Respiration rate") })
                    }

                    item {
                        TextField(value = bodyTemperature, onValueChange = {
                            bodyTemperature = it
                        }, label = { Text("Body temperature") })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val healthIndicators = HealthIndicators(
                    bloodGlucose = bloodGlucose.toDoubleOrNull() ?: 0.00,
                    heartRate = heartRate.toIntOrNull() ?: 0,
                    respirationRate = respirationRate.toIntOrNull() ?: 0,
                    bodyTemperature = bodyTemperature.toDoubleOrNull() ?: 0.00,
                    bloodPressure = bloodPressure.ifEmpty { "0/0" }
                )

                val goalCreateRequest = HealthCreateRequest(healthIndicators)
                onCreate(goalCreateRequest)
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun HealthActionButton(
    buttonText: String,
    onClickAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(3, 6, 21)),
        Alignment.TopCenter
    ) {
        Button(
            onClick = onClickAction,
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(3, 6, 33),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 20.dp, top = 20.dp)
                .height(50.dp)

        ) {
            Text(buttonText)
        }
    }
}