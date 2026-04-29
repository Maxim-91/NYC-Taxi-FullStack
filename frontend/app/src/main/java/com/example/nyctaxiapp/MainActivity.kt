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
import com.example.nyctaxiapp.ui.AnalyticsScreen
import com.example.nyctaxiapp.ui.LocationsScreen
import com.example.nyctaxiapp.ui.ManagementScreen

/** Entry point of the application.
 *  Inherits from ComponentActivity to provide Jetpack Compose support. **/
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display (drawing content behind status/navigation bars)
        enableEdgeToEdge()

        setContent {
            // Apply the custom application theme
            NYCTaxiAppTheme {
                NYCTaxiAppApp()
            }
        }
    }
}

/** Main application composable that handles global navigation logic.
 *  Uses adaptive NavigationSuiteScaffold to automatically switch between
 *  Navigation Bar (bottom) and Navigation Rail (side) based on screen size. **/
@Composable
fun NYCTaxiAppApp() {
    /** avigation state: current active screen.
     *  rememberSaveable ensures the state persists through configuration changes (e.g., rotation). **/
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.ANALYTICS) }

    // Adaptive scaffold for top-level navigation
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // Iterate through all destinations defined in the enum to create menu items
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        // Main content area of the application
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Box handles innerPadding to avoid content overlapping with the navigation bar
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.ANALYTICS -> AnalyticsScreen(
                        // Manual navigation callbacks for screen internal buttons
                        onNavigateToManagement = { currentDestination = AppDestinations.MANAGEMENT},
                        onNavigateToLocations = { currentDestination = AppDestinations.LOCATIONS })

                    AppDestinations.LOCATIONS -> LocationsScreen(
                        onNavigateToAnalytics = { currentDestination = AppDestinations.ANALYTICS },
                        onNavigateToManagement = { currentDestination = AppDestinations.MANAGEMENT })

                    AppDestinations.MANAGEMENT -> ManagementScreen(
                        onNavigateToAnalytics = { currentDestination = AppDestinations.ANALYTICS },
                        onNavigateToLocations = { currentDestination = AppDestinations.LOCATIONS })
                }
            }
        }
    }
}

/** Enumeration of available destinations within the app.
 *  Each entry contains the display label and the associated icon. **/
enum class AppDestinations(val label: String, val icon: ImageVector) {
    MANAGEMENT("Management", Icons.Default.Build),
    ANALYTICS("Analytics", Icons.Default.Info),
    LOCATIONS("Locations", Icons.Default.LocationOn),
}
