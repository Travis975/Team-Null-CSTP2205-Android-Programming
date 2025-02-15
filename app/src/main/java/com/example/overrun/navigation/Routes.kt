package com.example.overrun.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.overrun.ui.screens.DetailsScreen
import com.example.overrun.ui.screens.HomeScreen
import com.example.overrun.ui.screens.MainMenuScreen

@Composable
fun AppRoutes() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("details") { DetailsScreen(navController) }
        composable("mainMenu") { MainMenuScreen(navController) }
    }
}