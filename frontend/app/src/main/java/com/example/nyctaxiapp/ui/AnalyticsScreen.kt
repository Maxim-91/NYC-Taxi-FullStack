package com.example.nyctaxiapp.ui

import android.graphics.Paint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import com.example.nyctaxiapp.AnalyticsViewModel
import com.example.nyctaxiapp.AvgAmountItem
import com.example.nyctaxiapp.NavigationArrow

/** Main screen for displaying taxi trip analytics.
 *  Handles user interaction for date selection, granularity steps, and data visualization. **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateToManagement: () -> Unit,
    onNavigateToLocations: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val context = LocalContext.current

    // State management for the Date Picker dialog
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Observe UI state from the ViewModel
    val chartData by viewModel.chartData.collectAsState()
    val dt by viewModel.selectedDt.collectAsState()
    val step by viewModel.selectedStep.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // UI Logic: Show DatePicker Dialog when triggered
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) {
                        val cal = Calendar.getInstance().apply { timeInMillis = selected }
                        val year = cal.get(Calendar.YEAR)
                        // Validation: Restrict data range to specific years
                        if (year in 2020..2025) {
                            viewModel.updateDt(selected)
                        } else {
                            Toast.makeText(context, "The selected date must be between 2020 and 2025", Toast.LENGTH_LONG).show()
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // 1. Header: Navigation and Screen Title

        // Top bar with navigation arrows and screen title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left navigation - points to Management
            NavigationArrow(
                label = "Management",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onNavigateToManagement
            )

            // Center Title
            Text("Analytics", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Blue)

            // Right navigation - points to Locations
            NavigationArrow(
                label = "Locations",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onNavigateToLocations
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Date Selection Button: Changes color based on selection state
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
                text = if (isDtSelected) "Selected date: ${viewModel.getFormattedDate()}" else "Select a date (2020-2025 years)",
                color = if (isDtSelected) Color.White else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Step Selection Label
        Text("Select: year, month or day", color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Step Buttons: Trigger data refetch in the ViewModel
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            StepButton("Year", step == "year") { viewModel.updateStep("year") }
            Spacer(modifier = Modifier.width(8.dp))
            StepButton("Month", step == "month") { viewModel.updateStep("month") }
            Spacer(modifier = Modifier.width(8.dp))
            StepButton("Day", step == "day") { viewModel.updateStep("day") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Dynamic Data Display Area
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFF5F5F5)).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val errorMessage by viewModel.errorMessage.collectAsState()

            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                chartData.isNotEmpty() -> TaxiLineChart(chartData, step)
                else -> Text("No data available. Please select a date.", color = Color.Gray)
            }
        }
    }
}

/** Reusable component for step selection buttons. **/
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

/** Custom-drawn Line Chart using Jetpack Compose Canvas.
 *  Dynamically scales Y-axis based on integer ranges of the data provided. **/
@Composable
fun TaxiLineChart(data: List<AvgAmountItem>, step: String) {

    // Data range calculation for the Y-axis scale
    val minDataVal = data.minOfOrNull { it.avg_amount }?.toInt() ?: 0
    val maxDataVal = data.maxOfOrNull { it.avg_amount }?.toInt() ?: 10

    // Adding 1-unit padding to the top and bottom for visual clarity
    val minValY = (minDataVal - 1).toFloat()
    val maxValY = (maxDataVal + 1).toFloat()
    val rangeY = maxValY - minValY

    // Determine the number of grid lines (one for each integer step)
    val numberOfSteps = (maxValY - minValY).toInt()

    // Dynamic X-axis labeling based on the selected resolution
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

        // Custom drawing area for the chart
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(start = 50.dp, bottom = 60.dp, end = 20.dp, top = 10.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // --- DRAW AXES ---
            drawLine(Color.Black, Offset(0f, canvasHeight), Offset(canvasWidth, canvasHeight), strokeWidth = 2.dp.toPx())
            drawLine(Color.Black, Offset(0f, 0f), Offset(0f, canvasHeight), strokeWidth = 2.dp.toPx())

            // --- DRAW Y-AXIS GRID AND LABELS ---
            val paint = Paint().apply {
                color = android.graphics.Color.GRAY
                alpha = 80
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.RIGHT
            }

            for (i in 0..numberOfSteps) {
                val currentYValue = (minValY + i).toInt()
                val yPos = canvasHeight - (i.toFloat() / numberOfSteps) * canvasHeight

                // Draw horizontal grid line
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, yPos),
                    end = Offset(canvasWidth, yPos),
                    strokeWidth = 1.dp.toPx()
                )

                // Draw Y-axis coordinate text
                drawContext.canvas.nativeCanvas.drawText(
                    currentYValue.toString(),
                    -10.dp.toPx(),
                    yPos + 4.dp.toPx(),
                    paint
                )
            }

            // --- DRAW DATA LINE AND POINTS ---
            if (data.isNotEmpty()) {
                val distanceX = canvasWidth / (if (data.size > 1) data.size - 1 else 1)

                // Map data points to Canvas coordinates
                val points = data.mapIndexed { i, item ->
                    val fractionY = (item.avg_amount - minValY) / rangeY
                    Offset(i * distanceX, canvasHeight - fractionY * canvasHeight)
                }

                // Connect points with lines and draw circles at nodes
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

            // --- DRAW LABELS AND AXIS TITLES ---
            paint.textAlign = Paint.Align.CENTER
            paint.color = android.graphics.Color.BLACK
            paint.alpha = 255

            // X-axis Resolution Title
            drawContext.canvas.nativeCanvas.drawText(xAxisLabel, canvasWidth / 2, canvasHeight + 45.dp.toPx(), paint)

            // Y-axis Title (Rotated 90 degrees)
            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(-90f, -40.dp.toPx(), canvasHeight / 2)
            drawContext.canvas.nativeCanvas.drawText("Average taxi trips (avg_amount)", -40.dp.toPx(), canvasHeight / 2, paint)
            drawContext.canvas.nativeCanvas.restore()

            // X-axis Time Labels (e.g., hours or days)
            // Skip labels if there are too many (density control)
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

