package com.example.healthmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.data.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: HealthViewModel,
    recordId: Int?,          // null = add mode, a number = edit mode
    onDone: () -> Unit
) {
    val isEdit = recordId != null

    var date by rememberSaveable { mutableStateOf(viewModel.todayDate()) }
    var steps by rememberSaveable { mutableStateOf("") }
    var calories by rememberSaveable { mutableStateOf("") }
    var water by rememberSaveable { mutableStateOf("") }

    var stepsError by rememberSaveable { mutableStateOf<String?>(null) }
    var caloriesError by rememberSaveable { mutableStateOf<String?>(null) }
    var waterError by rememberSaveable { mutableStateOf<String?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var loaded by rememberSaveable { mutableStateOf(false) }

    // edit mode: load the existing record once and fill the fields
    LaunchedEffect(recordId) {
        if (recordId != null && !loaded) {
            viewModel.getRecord(recordId)?.let { record ->
                date = record.date
                steps = record.steps.toString()
                calories = record.calories.toString()
                water = record.water.toString()
            }
            loaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEdit) "Edit Record" else "Add Record",
            style = MaterialTheme.typography.headlineMedium
        )

        // date (chosen with a date picker, so it is always valid)
        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = steps,
            onValueChange = {
                if (it.all { c -> c.isDigit() } && it.length <= 6) {
                    steps = it
                    stepsError = null
                }
            },
            label = { Text("Steps") },
            isError = stepsError != null,
            supportingText = { stepsError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calories,
            onValueChange = {
                if (it.all { c -> c.isDigit() } && it.length <= 5) {
                    calories = it
                    caloriesError = null
                }
            },
            label = { Text("Calories burned (kcal)") },
            isError = caloriesError != null,
            supportingText = { caloriesError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = water,
            onValueChange = {
                if (it.all { c -> c.isDigit() } && it.length <= 5) {
                    water = it
                    waterError = null
                }
            },
            label = { Text("Water intake (ml)") },
            isError = waterError != null,
            supportingText = { waterError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.weight(1f)
            ) { Text("Cancel") }

            Button(
                onClick = {
                    stepsError = validateNumber(steps, "Steps", 100000)
                    caloriesError = validateNumber(calories, "Calories", 20000)
                    waterError = validateNumber(water, "Water", 20000)

                    if (stepsError == null && caloriesError == null && waterError == null) {
                        val record = HealthRecord(
                            id = recordId ?: 0,
                            date = date,
                            steps = steps.toInt(),
                            calories = calories.toInt(),
                            water = water.toInt()
                        )
                        if (isEdit) viewModel.updateRecord(record) else viewModel.addRecord(record)
                        onDone()
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text(if (isEdit) "Update" else "Save") }
        }
    }

    // date picker dialog
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateToMillis(date))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { date = millisToDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

// returns an error message, or null when the value is valid
private fun validateNumber(text: String, label: String, max: Int): String? {
    if (text.isBlank()) return "$label is required"
    val value = text.toIntOrNull() ?: return "Enter a valid number"
    if (value < 0) return "$label cannot be negative"
    if (value > max) return "$label must be $max or less"
    return null
}

private fun utcFormat() =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun dateToMillis(date: String): Long? =
    try { utcFormat().parse(date)?.time } catch (e: Exception) { null }

private fun millisToDate(millis: Long): String = utcFormat().format(Date(millis))