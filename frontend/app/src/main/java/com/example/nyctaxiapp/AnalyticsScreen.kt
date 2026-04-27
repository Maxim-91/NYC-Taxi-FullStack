package com.example.nyctaxiapp

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateForward: () -> Unit,
    vm: AnalyticsViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val chartData by vm.chartData.collectAsState()
    val dt by vm.selectedDt.collectAsState()
    val step by vm.selectedStep.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) {
                        val cal = Calendar.getInstance().apply { timeInMillis = selected }
                        val year = cal.get(Calendar.YEAR)
                        if (year in 2020..2025) {
                            vm.updateDt(selected)
                        } else {
                            Toast.makeText(context, "Дата має бути в межах між 2020 та 2025 роками", Toast.LENGTH_LONG).show()
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 1. Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                Text("Management", fontSize = 10.sp)
            }
            Text("Analytics", color = Color.Blue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onNavigateForward) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward") }
                Text("Locations", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Date Button
        val isDtSelected = dt != null
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDtSelected) Color.Blue else Color.LightGray
            )
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = if (isDtSelected) Color.White else Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isDtSelected) "Selected date: ${vm.getFormattedDate()}" else "Select a date (2020-2025 years)",
                color = if (isDtSelected) Color.White else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Step Label
        Text("Select Step: year, month or day", color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Step Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            StepButton("Year", step == "year") { vm.updateStep("year") }
            Spacer(modifier = Modifier.width(8.dp))
            StepButton("Month", step == "month") { vm.updateStep("month") }
            Spacer(modifier = Modifier.width(8.dp))
            StepButton("Day", step == "day") { vm.updateStep("day") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Chart View
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFF5F5F5)).padding(16.dp),
            contentAlignment = Alignment.Center
        )
        {
            val errorMessage by vm.errorMessage.collectAsState() // додайте цей state у ViewModel

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
            } else if (chartData.isNotEmpty()) {
                println("Data size: ${chartData.size}")
                BarChart(chartData)
            } else {
                Text("Дані відсутні або оберіть дату", color = Color.Gray)
            }
        }
    }
}

@Composable
fun StepButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Blue else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) { Text(text) }
}

@Composable
fun BarChart(data: List<AvgAmountItem>) {
    // Додаємо невеликий відступ зверху, щоб стовпчик не впирався в край
    val maxVal = (data.maxOfOrNull { it.avg_amount } ?: 1f) * 1.2f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 20.dp.toPx()
        val barWidth = (size.width - (spacing * (data.size + 1))) / data.size

        data.forEachIndexed { i, item ->
            val barHeight = (item.avg_amount / maxVal) * size.height
            val x = spacing + i * (barWidth + spacing)

            // Малюємо стовпчик
            drawRect(
                color = Color(0xFF2196F3),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )

            // Малюємо підпис (номер місяця/дня/години)
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    item.timeLabel.toString(),
                    x + barWidth / 2,
                    size.height + 20.dp.toPx(),
                    paint
                )
            }
        }
    }
}
