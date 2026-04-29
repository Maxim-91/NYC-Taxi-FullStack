package com.example.nyctaxiapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nyctaxiapp.ManagementViewModel
import com.example.nyctaxiapp.NavigationArrow
import com.example.nyctaxiapp.PaymentType
import com.example.nyctaxiapp.RateCode

@Composable
fun ManagementScreen(
    onNavigateToAnalytics: () -> Unit,
    onNavigateToLocations: () -> Unit,
    viewModel: ManagementViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Payment Methods", "Rate Codes")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // --- Header ---
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            NavigationArrow("Analytics", Icons.AutoMirrored.Filled.ArrowBack, onNavigateToAnalytics)
            Text("Management", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
            NavigationArrow("Locations", Icons.AutoMirrored.Filled.ArrowForward, onNavigateToLocations)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        // --- Content ---
        when (selectedTab) {
            0 -> PaymentTabContent(viewModel)
            1 -> RateCodeTabContent(viewModel)
        }
    }
}

@Composable
fun PaymentTabContent(viewModel: ManagementViewModel) {
    val items by viewModel.paymentTypes.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<PaymentType?>(null) }
    var deleteItem by remember { mutableStateOf<PaymentType?>(null) }

    GenericManagementTab(
        items = items,
        getItemId = { it.id },
        getItemLabel = { it.payment_type },
        onAdd = { showAdd = true },
        onEdit = { editItem = it },
        onDelete = { deleteItem = it }
    )

    if (showAdd) {
        ItemActionDialog(
            title = "Add Payment Method",
            existingLabels = items.map { it.payment_type },
            onDismiss = { showAdd = false },
            onConfirm = { viewModel.addPaymentType(it); showAdd = false }
        )
    }

    editItem?.let { item ->
        ItemActionDialog(
            title = "Edit Payment Method",
            initialText = item.payment_type,
            idDisplay = item.id,
            existingLabels = items.map { it.payment_type },
            onDismiss = { editItem = null },
            onConfirm = { viewModel.editPaymentType(item.id, it); editItem = null }
        )
    }

    deleteItem?.let { item ->
        DeleteConfirmDialog(
            title = "Delete Payment",
            info = "${item.id}: ${item.payment_type}",
            onDismiss = { deleteItem = null },
            onConfirm = { viewModel.deletePaymentType(item.id); deleteItem = null }
        )
    }
}

@Composable
fun RateCodeTabContent(viewModel: ManagementViewModel) {
    val items by viewModel.rateCodes.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<RateCode?>(null) }
    var deleteItem by remember { mutableStateOf<RateCode?>(null) }

    GenericManagementTab(
        items = items,
        getItemId = { it.id },
        getItemLabel = { it.code },
        onAdd = { showAdd = true },
        onEdit = { editItem = it },
        onDelete = { deleteItem = it }
    )

    if (showAdd) {
        ItemActionDialog(
            title = "Add Rate Code",
            existingLabels = items.map { it.code },
            onDismiss = { showAdd = false },
            onConfirm = { viewModel.addRateCode(it); showAdd = false }
        )
    }

    editItem?.let { item ->
        ItemActionDialog(
            title = "Edit Rate Code",
            initialText = item.code,
            idDisplay = item.id,
            existingLabels = items.map { it.code },
            onDismiss = { editItem = null },
            onConfirm = { viewModel.editRateCode(item.id, it); editItem = null }
        )
    }

    deleteItem?.let { item ->
        DeleteConfirmDialog(
            title = "Delete Rate Code",
            info = "${item.id}: ${item.code}",
            onDismiss = { deleteItem = null },
            onConfirm = { viewModel.deleteRateCode(item.id); deleteItem = null }
        )
    }
}
