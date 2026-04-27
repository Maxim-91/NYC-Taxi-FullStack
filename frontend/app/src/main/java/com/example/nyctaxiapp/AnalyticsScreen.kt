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
                            Toast.makeText(context, "The selected date from the calendar must be between 2020 and 2025 years", Toast.LENGTH_LONG).show()
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
        Text("Select: year, month or day", color = Color.Black)

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

            if (isLoading)
            {
                CircularProgressIndicator()
            }
            else if (errorMessage != null)
            {
                Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
            }
            else if (chartData.isNotEmpty())
            {
                TaxiLineChart(chartData, step)
            }
            else
            {
                Text("No data available or select a date", color = Color.Gray)
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
fun TaxiLineChart(data: List<AvgAmountItem>, step: String) {
    // 1. Обчислюємо точні межі для цілих чисел
    val minDataVal = data.minOfOrNull { it.avg_amount }?.toInt() ?: 0
    val maxDataVal = data.maxOfOrNull { it.avg_amount }?.toInt() ?: 10

    // Додаємо відступ в 1 одиницю зверху та знизу для візуального комфорту
    val minValY = (minDataVal - 1).toFloat()
    val maxValY = (maxDataVal + 1).toFloat()
    val rangeY = maxValY - minValY

    // 2. Визначаємо кількість рисок (кроків) як різницю між цілими числами
    val numberOfSteps = (maxValY - minValY).toInt()

    val xAxisLabel = when (step) {
        "year" -> "Months (pu_month)"
        "month" -> "Days (pu_day)"
        "day" -> "Hours (pu_hour)"
        else -> ""
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Taxi trip statistics in New York city",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(start = 50.dp, bottom = 60.dp, end = 20.dp, top = 10.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Малюємо основні осі
            drawLine(Color.Black, Offset(0f, canvasHeight), Offset(canvasWidth, canvasHeight), strokeWidth = 2.dp.toPx())
            drawLine(Color.Black, Offset(0f, 0f), Offset(0f, canvasHeight), strokeWidth = 2.dp.toPx())

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                alpha = 80
                textSize = 10.sp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
            }

            // 3. Малюємо риски для КОЖНОГО цілого числа
            for (i in 0..numberOfSteps) {
                val currentYValue = (minValY + i).toInt()
                val yPos = canvasHeight - (i.toFloat() / numberOfSteps) * canvasHeight

                // Горизонтальна лінія сітки
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, yPos),
                    end = Offset(canvasWidth, yPos),
                    strokeWidth = 1.dp.toPx()
                )

                // Текст координати Y
                drawContext.canvas.nativeCanvas.drawText(
                    currentYValue.toString(),
                    -10.dp.toPx(),
                    yPos + 4.dp.toPx(),
                    paint
                )
            }

            // Малюємо графік-лінію
            if (data.isNotEmpty()) {
                val distanceX = canvasWidth / (if (data.size > 1) data.size - 1 else 1)
                val points = data.mapIndexed { i, item ->
                    // Розрахунок позиції відносно динамічної шкали
                    val fractionY = (item.avg_amount - minValY) / rangeY
                    Offset(i * distanceX, canvasHeight - fractionY * canvasHeight)
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color(0xFF2196F3),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.5.dp.toPx()
                    )
                    drawCircle(Color.Blue, radius = 3.dp.toPx(), center = points[i])
                }
                drawCircle(Color.Blue, radius = 3.dp.toPx(), center = points.last())
            }

            // Підписи осей (чорний колір)
            paint.textAlign = android.graphics.Paint.Align.CENTER
            paint.color = android.graphics.Color.BLACK
            paint.alpha = 255

            // Вісь X
            drawContext.canvas.nativeCanvas.drawText(xAxisLabel, canvasWidth / 2, canvasHeight + 45.dp.toPx(), paint)

            // Вісь Y
            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(-90f, -40.dp.toPx(), canvasHeight / 2)
            drawContext.canvas.nativeCanvas.drawText("Average taxi trips (avg_amount)", -40.dp.toPx(), canvasHeight / 2, paint)
            drawContext.canvas.nativeCanvas.restore()

            // Значення X (місяці/дні/години)
            val labelStep = if (data.size > 12) 2 else 1
            data.forEachIndexed { i, item ->
                if (i % labelStep == 0) {
                    val distanceX = canvasWidth / (if (data.size > 1) data.size - 1 else 1)
                    drawContext.canvas.nativeCanvas.drawText(
                        item.timeLabel.toString(),
                        i * distanceX,
                        canvasHeight + 20.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}
