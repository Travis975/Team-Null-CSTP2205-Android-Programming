package com.example.overrun.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.Routes
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Overrun.ui.screens.DetailsScreen
import com.example.Overrun.ui.screens.HomeScreen

@Composable
fun AppRoutes() {
    val navController = rememberNavController()

    NavRoutes(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("details") { DetailsScreen(navController) }
    }
}