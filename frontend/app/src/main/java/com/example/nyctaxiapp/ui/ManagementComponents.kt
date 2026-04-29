package com.example.nyctaxiapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A generic reusable Tab content for any management item.
 *  @param items List of items to display (PaymentType or RateCode)
 *  @param getItemLabel Function to extract the text to show (name or code)
 *  @param getItemId Function to extract the ID
 *  @param onAdd Click handler for the Add button
 *  @param onEdit Click handler for the Edit icon
 *  @param onDelete Click handler for the Delete icon **/
@Composable
fun <T> GenericManagementTab(
    items: List<T>,
    getItemId: (T) -> Int,
    getItemLabel: (T) -> String,
    onAdd: () -> Unit,
    onEdit: (T) -> Unit,
    onDelete: (T) -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)) {
                Text("+ Add")
            }
        }

        LazyColumn {
            items(items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Displays "ID. Name" or "ID. Code"
                    Text("${getItemId(item)}. ${getItemLabel(item)}", modifier = Modifier.weight(1f))

                    IconButton(onClick = { onEdit(item) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                    }
                    IconButton(onClick = { onDelete(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

/** Universal Dialog for adding or editing items. **/
@Composable
fun ItemActionDialog(
    title: String,
    initialText: String = "",
    idDisplay: Int? = null,
    existingLabels: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(initialText) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (idDisplay != null) {
                    Text("ID: $idDisplay", color = Color.Gray)
                }
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it; error = "" },
                    label = { Text("Value") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                val trimmed = textValue.trim()
                when {
                    trimmed.isEmpty() -> error = "Field cannot be empty"
                    trimmed.all { it.isDigit() } -> error = "Cannot be only digits"
                    // Check for duplicates excluding the current value if it's an edit
                    existingLabels.any { it.equals(trimmed, true) } && trimmed != initialText ->
                        error = "This value already exists"
                    else -> onConfirm(trimmed)
                }
            }) { Text("Ok") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(containerColor = Color.Red)) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

/**  Dialog for deleting items. **/
@Composable
fun DeleteConfirmDialog(title: String, info: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text("Are you sure about deleting: $info?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)) {
                Text("Cancel")
            }
        }
    )
}
