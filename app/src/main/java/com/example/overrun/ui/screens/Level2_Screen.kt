package com.example.overrun.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.eGameStage

@Composable
fun Level2_Screen(navController: NavController, gameViewModel: GameViewModel) {

    LevelBase_Screen(navController, gameViewModel, eGameStage.eStage2)
}

@Preview(showBackground = true)
@Composable
fun PreviewLevel2() {
    val gameViewModel: GameViewModel = viewModel()
    Level2_Screen(navController = rememberNavController(), gameViewModel)
}