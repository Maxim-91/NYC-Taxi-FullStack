package com.example.nyctaxiapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

/** Reusable navigation arrow with a label.
 *  Can be accessed from any screen in the project. */
/** Helper component for navigation icons with text labels. **/
@Composable
fun NavigationArrow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label)
        }
        Text(label, fontSize = 10.sp)
    }
}
