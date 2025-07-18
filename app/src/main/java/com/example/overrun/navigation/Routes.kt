package com.example.overrun.navigation

import SignInScreen
import SignUpScreen
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
import com.example.overrun.ui.screens.GameEndScreen
import com.example.overrun.ui.screens.HomeScreen
import com.example.overrun.ui.screens.LeaderBoardScreen
import com.example.overrun.ui.screens.Level1_Screen
import com.example.overrun.ui.screens.MainMenuScreen
import com.example.overrun.ui.screens.PrivacyScreen
import com.example.overrun.ui.screens.StartGameScreen
import com.example.overrun.ui.screens.TermsScreen
import com.example.overrun.ui.screens.Level2_Screen
import com.example.overrun.ui.screens.Level3_Screen
import com.example.overrun.ui.screens.SettingsScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppRoutes() {
    val navController = rememberNavController()

    // This gameViewModel is the same data instance across the same navigation tree
    val gameViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = HOME.path) {
        composable(HOME.path) { HomeScreen(navController) }
        composable(SIGNUP.path) { SignUpScreen(navController) }
        composable(SIGNIN.path) { SignInScreen(navController) }
        composable(MAIN_MENU.path) { MainMenuScreen(navController) }
        composable(PRIVACY.path) { PrivacyScreen(navController) }
        composable(TERMS.path) { TermsScreen(navController) }
        composable(LEVEL_2.path) { Level2_Screen(navController, gameViewModel) }
        composable(LEVEL_1.path) { Level1_Screen(navController, gameViewModel) }
        composable(START_GAME.path) { StartGameScreen(navController, gameViewModel) }
        composable(GAME_END.path) { GameEndScreen(navController, gameViewModel) }
        composable(LEADERBOARD.path) { LeaderBoardScreen(navController) }
        composable(CONTROLS.path) { ControlsScreen(navController)  }
        composable(SETTINGS.path) { SettingsScreen(navController) }
        composable(LEVEL_3.path) { Level3_Screen(navController, gameViewModel) }
    }
}