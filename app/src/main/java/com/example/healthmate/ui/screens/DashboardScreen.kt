package com.example.healthmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.healthmate.viewmodel.HealthViewModel

private val StepsGreen = Color(0xFF388E3C)
private val CaloriesRed = Color(0xFFD32F2F)
private val WaterBlue = Color(0xFF1976D2)

@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onAddClick: () -> Unit
) {
    val summary by viewModel.todaySummary.collectAsState()
    val noData = summary.steps == 0 && summary.calories == 0 && summary.water == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Text("Today: ${viewModel.todayDate()}", style = MaterialTheme.typography.titleMedium)

        SummaryCard(
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            label = "Steps",
            value = "%,d".format(summary.steps),
            color = StepsGreen
        )
        SummaryCard(
            icon = Icons.Filled.LocalFireDepartment,
            label = "Calories burned",
            value = "%,d kcal".format(summary.calories),
            color = CaloriesRed
        )
        SummaryCard(
            icon = Icons.Filled.WaterDrop,
            label = "Water intake",
            value = "%,d ml".format(summary.water),
            color = WaterBlue
        )

        if (noData) {
            Text("No record added for today yet.")
        }

        Spacer(Modifier.height(4.dp))
        Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
            Text("Add today's record")
        }
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = color
                )
            }
        }
    }
}