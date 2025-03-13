package com.example.overrun.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.*
import com.example.overrun.ui.screens.ControlsScreen
import com.example.overrun.ui.screens.SignUpScreen
import com.example.overrun.ui.screens.HomeScreen
import com.example.overrun.ui.screens.MainMenuScreen
import com.example.overrun.ui.screens.StartGameScreen
import com.example.overrun.ui.screens.TestCharacterGameScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppRoutes() {
    val navController = rememberNavController()

    // This gameViewModel is the same data instance across the same navigation tree
    val gameViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = HOME.path) {
        composable(HOME.path) { HomeScreen(navController) }
        composable(SIGNUP.path) { SignUpScreen(navController) }
        composable(MAIN_MENU.path) { MainMenuScreen(navController) }
        composable(TEST_CHARACTER.path) { TestCharacterGameScreen(navController, gameViewModel) }
        composable(START_GAME.path) { StartGameScreen(navController) }
        composable(CONTROLS.path) { ControlsScreen(navController)  }
    }
}