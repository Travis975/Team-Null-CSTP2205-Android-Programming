package com.example.overrun.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.overrun.enitities.Route.*
import com.example.overrun.ui.screens.SignInScreen
import com.example.overrun.ui.screens.HomeScreen
import com.example.overrun.ui.screens.MainMenuScreen
import com.example.overrun.ui.screens.TestCharacterGameScreen

@Composable
fun AppRoutes() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME.path) {
        composable(HOME.path) { HomeScreen(navController) }
        composable(SIGNIN.path) { SignInScreen(navController) }
        composable(MAIN_MENU.path) { MainMenuScreen(navController) }
        composable(TEST_CHARACTER.path) { TestCharacterGameScreen(navController) }
    }
}