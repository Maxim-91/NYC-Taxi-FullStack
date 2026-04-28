package com.example.nyctaxiapp

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationsScreen(
    onNavigateToAnalytics: () -> Unit,
    onNavigateToManagement: () -> Unit,
    viewModel: LocationsViewModel = viewModel()
) {
    val zones by viewModel.filteredZones.collectAsState()
    val boroughs by viewModel.selectedBoroughs.collectAsState()
    val services by viewModel.selectedServiceZones.collectAsState()
    val isAllBOn by viewModel.isAllBoroughsOn.collectAsState(initial = true)
    val isAllSOn by viewModel.isAllServiceZonesOn.collectAsState(initial = true)

    var searchInput by remember { mutableStateOf("") }
    var isFiltersVisible by remember { mutableStateOf(true) }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 1. Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavigationArrow(label = "Analytics", icon = Icons.Default.ArrowBack, onClick = onNavigateToAnalytics)
            Text("Locations", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
            NavigationArrow(label = "Management", icon = Icons.Default.ArrowForward, onClick = onNavigateToManagement)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Search Bar
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { input ->
                    // Обмеження символів: Латиниця, пробіл, /, кома. Точка -> Кома.
                    val filtered = input.replace(".", ",").filter { it.isLetter() || it == ' ' || it == '/' || it == ',' }
                    searchInput = filtered
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter text to Find zone_name", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search // 1. Вказуємо клавіатурі, що кнопка Enter має бути іконкою пошуку
                ),
                // 2. Описуємо дію при натисканні на цю кнопку
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchQuery.value = searchInput // Виконуємо пошук
                        focusManager.clearFocus() // ПРИБИРАЄМО клавіатуру та фокус (це імітує завершення введення)
                    }
                ),
                singleLine = true // Enter не намагався перенести рядок
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.searchQuery.value = searchInput },
                modifier = Modifier.width(100.dp)
            ) { Text("Search") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. Collapsible Filters
        IconButton(onClick = { isFiltersVisible = !isFiltersVisible }) {
            Icon(if (isFiltersVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
        }

        AnimatedVisibility(visible = isFiltersVisible) {
            Column {
                FilterBox(title = "Filter by borough") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterCheckbox(label = "All", checked = isAllBOn, enabled = !isAllBOn, onCheckedChange = { viewModel.setAllBoroughsOn() })
                        boroughs.forEach { (name, checked) ->
                            FilterCheckbox(label = name, checked = checked, onCheckedChange = { viewModel.toggleBorough(name, it) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FilterBox(title = "Filter by service zone") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterCheckbox(label = "All", checked = isAllSOn, enabled = !isAllSOn, onCheckedChange = { viewModel.setAllServiceZonesOn() })
                        services.forEach { (name, checked) ->
                            FilterCheckbox(label = name, checked = checked, onCheckedChange = { viewModel.toggleServiceZone(name, it) })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Results Table
        Text("List of searched zone names", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Table Header
        Row(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            TableCell(text = "ID", weight = 0.15f)
            TableCell(text = "Zone Name", weight = 0.35f)
            TableCell(text = "Service", weight = 0.25f)
            TableCell(text = "Borough", weight = 0.25f)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().border(1.dp, Color.Gray)) {
            items(zones) { zone ->
                Row(Modifier.fillMaxWidth().padding(8.dp)) {
                    TableCell(text = zone.LocationID.toString(), weight = 0.15f)
                    TableCell(text = zone.zone_name, weight = 0.35f)
                    TableCell(text = zone.service_zone_name, weight = 0.25f)
                    TableCell(text = zone.borough_name, weight = 0.25f)
                }
                Divider(color = Color.LightGray)
            }
        }
    }
}

@Composable
fun NavigationArrow(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) { Icon(icon, contentDescription = label) }
        Text(label, fontSize = 10.sp)
    }
}

@Composable
fun FilterBox(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        content()
    }
}

@Composable
fun FilterCheckbox(label: String, checked: Boolean, enabled: Boolean = true, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = if (enabled) onCheckedChange else null, enabled = enabled)
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(text, modifier = Modifier.weight(weight), fontSize = 12.sp)
}
