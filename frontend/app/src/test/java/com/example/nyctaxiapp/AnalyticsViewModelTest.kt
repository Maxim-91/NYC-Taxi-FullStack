package com.example.nyctaxiapp

import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsViewModelTest {
    @Test
    fun `test initial step is year`() {
        val viewModel = AnalyticsViewModel()
        assertEquals("year", viewModel.selectedStep.value)
    }

    @Test
    fun `test step update to month`() {
        val viewModel = AnalyticsViewModel()
        viewModel.updateStep("month")
        assertEquals("month", viewModel.selectedStep.value)
    }

    @Test
    fun `test date update`() {
        val viewModel = AnalyticsViewModel()
        val testTime = 1640995200000L
        viewModel.updateDt(testTime)
        assertEquals(testTime, viewModel.selectedDt.value)
    }
}
