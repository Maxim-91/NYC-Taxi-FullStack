package com.example.nyctaxiapp

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for the AnalyticsViewModel.
 *  These tests ensure that the business logic and state transitions are working correctly within
 *  the ViewModel function correctly in isolation. **/
class AnalyticsViewModelTest {

    /** Verifies that when the ViewModel is first created,
     * the default granularity (step) is set to "year". **/
    @Test
    fun `test initial step is year`() {
        val viewModel = AnalyticsViewModel()
        // Check if the default value of the selectedStep StateFlow is "year"
        assertEquals("year", viewModel.selectedStep.value)
    }

    /** Verifies that the updateStep function correctly changes the state of
     * the selected granularity. **/
    @Test
    fun `test step update to month`() {
        val viewModel = AnalyticsViewModel()

        // Trigger the step update
        viewModel.updateStep("month")

        // Assert that the state was updated successfully
        assertEquals("month", viewModel.selectedStep.value)
    }

    /** Verifies that the updateDt function correctly updates the selected date timestamp. **/
    @Test
    fun `test date update`() {
        val viewModel = AnalyticsViewModel()

        // Mock timestamp (e.g., January 1, 2022)
        val testTime = 1640995200000L

        // Trigger the date update
        viewModel.updateDt(testTime)

        // Assert that the StateFlow holds the correct timestamp
        assertEquals(testTime, viewModel.selectedDt.value)
    }
}
