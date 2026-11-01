package com.example.healthmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthmate.data.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel

private val CaloriesRed = Color(0xFFD32F2F)
private val WaterBlue = Color(0xFF1976D2)

@Composable
fun RecordListScreen(
    viewModel: HealthViewModel,
    onEdit: (Int) -> Unit = {}
) {
    val records by viewModel.records.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    var recordToDelete by remember { mutableStateOf<HealthRecord?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Health Records", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onSearchChange,
            label = { Text("Search by date (e.g. 2026-10)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        if (records.isEmpty()) {
            Text("No records found.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(records, key = { it.id }) { record ->
                    RecordItem(
                        record = record,
                        onEdit = { onEdit(record.id) },
                        onDelete = { recordToDelete = record }
                    )
                }
            }
        }
    }

    // delete confirmation dialog
    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete record?") },
            text = { Text("Delete the record for ${record.date}? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRecord(record)
                    recordToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun RecordItem(
    record: HealthRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.date, style = MaterialTheme.typography.titleMedium)
                Text("Steps: ${record.steps}")
                Text("Calories: ${record.calories} kcal", color = CaloriesRed)
                Text("Water: ${record.water} ml", color = WaterBlue)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit record")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete record")
            }
        }
    }
}