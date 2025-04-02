package com.example.overrun.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.MAIN_MENU
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ScreenControlAndMetrics(navController: NavController, gameViewModel: GameViewModel)
{
    // Timer state
    val isTimerRunning = gameViewModel.isTimerRunning.value

    val gameTime = remember { mutableStateOf(0) } // Tracks elapsed seconds

    // State to control the visibility of the pause menu
    val isPauseDialogVisible = remember { mutableStateOf(gameViewModel.gameMetricsAndCtrl.isGamePaused.value) }

    // Important, use Dispatchers.Default rather than the main thread
    // Timer logic with pause-resume control
    LaunchedEffect(isTimerRunning) {

        // use separate thread other than main thread
        withContext(Dispatchers.Default)
        {
            while (isTimerRunning) {
                delay(1000L) // Wait 1 second
                gameTime.value++ // Increment timer
            }
        }
    }

    // Screen Control
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Add pause icon to pull up the pause menu
            PauseIcon(
                navController = navController,
                gameViewModel = gameViewModel,
                onPause = {
                    isPauseDialogVisible.value = true
                    gameViewModel.gameMetricsAndCtrl.isGamePaused.value = true
                    gameViewModel.SetTimerRunStop(false) // Stop the timer on pause
                }
            )

            // Pause Menu Dialog
            if (isPauseDialogVisible.value) {
                PauseMenu(
                    onResume = {
                        isPauseDialogVisible.value = false
                        gameViewModel.gameMetricsAndCtrl.isGamePaused.value = false
                        gameViewModel.SetTimerRunStop(true) // Resume the timer
                    },
                    onQuit = {
                        navController.navigate(MAIN_MENU.path) // Navigate to the main menu
                    }
                )
            }
            // Display Timer and Hit Count outside the pause menu conditional
            // Ensure these are only shown when the game is not paused
            else
            {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time: ${gameTime.value}s",
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Kill Count: ${gameViewModel.gameMetricsAndCtrl.getEnemyKillCount()}",
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Lives: ${gameViewModel.hero.getLives()}",
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }


}