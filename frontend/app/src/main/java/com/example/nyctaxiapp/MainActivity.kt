package com.example.nyctaxiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nyctaxiapp.ui.theme.NYCTaxiAppTheme
import androidx.compose.foundation.layout.Box

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NYCTaxiAppTheme {
                NYCTaxiAppApp()
            }
        }
    }
}

@Composable
fun NYCTaxiAppApp() {
    // Analytics (HOME) тепер за замовчуванням
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.ANALYTICS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.ANALYTICS -> AnalyticsScreen(
                        onNavigateBack = { currentDestination = AppDestinations.MANAGEMENT },
                        onNavigateForward = { currentDestination = AppDestinations.LOCATIONS }
                    )
                    AppDestinations.MANAGEMENT -> Text("Management Screen Content")
                    AppDestinations.LOCATIONS -> Text("Locations Screen Content")
                }
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    MANAGEMENT("Management", Icons.Default.Build),
    ANALYTICS("Analytics", Icons.Default.Info),
    LOCATIONS("Locations", Icons.Default.LocationOn),
}
