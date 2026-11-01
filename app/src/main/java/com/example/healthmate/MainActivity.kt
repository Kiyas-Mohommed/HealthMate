package com.example.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.ui.screens.AddEditScreen
import com.example.healthmate.ui.screens.RecordListScreen
import com.example.healthmate.ui.theme.HealthMateTheme
import com.example.healthmate.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMateTheme {
                val viewModel: HealthViewModel = viewModel()
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // show every message the ViewModel sends
                LaunchedEffect(Unit) {
                    viewModel.messages.collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    floatingActionButton = {
                        if (currentRoute == "list") {
                            FloatingActionButton(onClick = { navController.navigate("add") }) {
                                Icon(Icons.Default.Add, contentDescription = "Add record")
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("list") {
                            RecordListScreen(
                                viewModel = viewModel,
                                onEdit = { id -> navController.navigate("edit/$id") }
                            )
                        }
                        composable("add") {
                            AddEditScreen(
                                viewModel = viewModel,
                                recordId = null,
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "edit/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { entry ->
                            AddEditScreen(
                                viewModel = viewModel,
                                recordId = entry.arguments?.getInt("id"),
                                onDone = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}