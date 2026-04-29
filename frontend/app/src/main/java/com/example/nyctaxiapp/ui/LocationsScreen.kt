package com.example.nyctaxiapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import com.example.nyctaxiapp.LocationsViewModel
import com.example.nyctaxiapp.NavigationArrow

/** Main Screen for displaying and filtering NYC Taxi Locations.
 *  Includes search functionality, collapsible filters for boroughs/service zones,
 *  and a paginated-style results table. **/
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationsScreen(
    onNavigateToAnalytics: () -> Unit,
    onNavigateToManagement: () -> Unit,
    viewModel: LocationsViewModel = viewModel()
) {
    // Collecting reactive states from ViewModel
    val zones by viewModel.filteredZones.collectAsState()
    val boroughs by viewModel.selectedBoroughs.collectAsState()
    val services by viewModel.selectedServiceZones.collectAsState()

    // States for "All" checkboxes logic (locked when true)
    val isAllBOn by viewModel.isAllBoroughsOn.collectAsState(initial = true)
    val isAllSOn by viewModel.isAllServiceZonesOn.collectAsState(initial = true)

    // UI Local states for search input and filter visibility
    var searchInput by remember { mutableStateOf("") }
    var isFiltersVisible by remember { mutableStateOf(true) }

    // Focus manager used to hide keyboard and clear focus after search actions
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- SECTION 1: Header & Navigation ---
        // Top bar with navigation arrows and screen title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left navigation - points to Analytics
            NavigationArrow(
                label = "Analytics",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onNavigateToAnalytics
            )

            // Center Title
            Text("Locations", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Blue)

            // Right navigation - points to Management
            NavigationArrow(
                label = "Management",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onNavigateToManagement
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 2: Search Bar ---
        // Input field for searching specific zone names with input validation
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { input ->
                    // Validation: Allows only Latin letters, spaces, slashes, and commas.
                    // Automatically converts dots to commas.
                    val filtered = input.replace(".", ",").filter {
                        it.isLetter() || it == ' ' || it == '/' || it == ','
                    }
                    searchInput = filtered
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter text to Find zone_name", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search // Replaces 'Enter' with a 'Search' magnifying glass icon
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchQuery.value = searchInput // Triggers search logic in ViewModel
                        focusManager.clearFocus() // Hides keyboard and removes focus
                    }
                ),
                singleLine = true // Prevents multi-line input and ensures ImeAction works
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.searchQuery.value = searchInput
                    focusManager.clearFocus()
                },
                modifier = Modifier.width(100.dp)
            ) { Text("Search") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SECTION 3: Collapsible Filters ---
        // Toggle button to expand/collapse the filter settings
        IconButton(onClick = { isFiltersVisible = !isFiltersVisible }) {
            Icon(
                imageVector = if (isFiltersVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle Filters"
            )
        }

        AnimatedVisibility(
            visible = isFiltersVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                // Filter by Borough Group
                FilterBox(title = "Filter by borough") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // "All" logic: remains 'on' if all items are selected. Becomes 'off' if one is unselected.
                        FilterCheckbox(
                            label = "All",
                            checked = isAllBOn,
                            enabled = !isAllBOn, // Locked if already active
                            onCheckedChange = { viewModel.setAllBoroughsOn() }
                        )
                        boroughs.forEach { (name, checked) ->
                            FilterCheckbox(
                                label = name,
                                checked = checked,
                                onCheckedChange = { viewModel.toggleBorough(name, it) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Filter by Service Zone Group
                FilterBox(title = "Filter by service zone") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterCheckbox(
                            label = "All",
                            checked = isAllSOn,
                            enabled = !isAllSOn,
                            onCheckedChange = { viewModel.setAllServiceZonesOn() }
                        )
                        services.forEach { (name, checked) ->
                            FilterCheckbox(
                                label = name,
                                checked = checked,
                                onCheckedChange = { viewModel.toggleServiceZone(name, it) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECTION 4: Results Table ---
        Text("List of searched zone names", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Header Row for the results table
        Row(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            TableCell(text = "ID", weight = 0.15f)
            TableCell(text = "Zone Name", weight = 0.35f)
            TableCell(text = "Service", weight = 0.25f)
            TableCell(text = "Borough", weight = 0.25f)
        }

        // Scrollable list displaying the filtered results
        LazyColumn(modifier = Modifier.fillMaxSize().border(1.dp, Color.Gray)) {
            items(zones) { zone ->
                Row(Modifier.fillMaxWidth().padding(8.dp)) {
                    TableCell(text = zone.LocationID.toString(), weight = 0.15f)
                    TableCell(text = zone.zone_name, weight = 0.35f)
                    TableCell(text = zone.service_zone_name, weight = 0.25f)
                    TableCell(text = zone.borough_name, weight = 0.25f)
                }
                HorizontalDivider(color = Color.LightGray)
            }
        }
    }
}

/** Styled container for a group of filters. **/
@Composable
fun FilterBox(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        content()
    }
}

/** Standardized Checkbox with a label for filtering options. **/
@Composable
fun FilterCheckbox(label: String, checked: Boolean, enabled: Boolean = true, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled
        )
        Text(label, fontSize = 12.sp)
    }
}

/** Helper for creating table cells with proportional weighting. **/
@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = 12.sp
    )
}
