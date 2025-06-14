package com.example.overrun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.overrun.enitities.Route.GAME_END
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.overrun.R

@Composable
fun ScreenControlAndMetrics(navController: NavController, gameViewModel: GameViewModel)
{
    // Timer state
    val isTimerRunning = gameViewModel.isTimerRunning.value

    val gameTime = remember { mutableStateOf(0) } // Tracks elapsed seconds

    var isGameOver by remember{mutableStateOf(false)}

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

            // if Hero die Or stage clear, navigate to GameOver
            if ((gameViewModel.hero.isDieFinished() || gameViewModel.gameMetricsAndCtrl.isStageClear())
                && !isGameOver)
            {
                isGameOver = true
                gameViewModel.gameMetricsAndCtrl.setTimeSurvived(gameTime.value.toString())
                navController.navigate(GAME_END.path)
            }

            // Pause Menu Dialog
            if (isPauseDialogVisible.value) {
                PauseMenu(
                    onResume = {
                        isPauseDialogVisible.value = false
                        gameViewModel.gameMetricsAndCtrl.isGamePaused.value = false
                        gameViewModel.SetTimerRunStop(true) // Resume the timer
                    },
                    onQuit = {
                        // Navigate to the game over screen
                        gameViewModel.gameMetricsAndCtrl.setTimeSurvived(gameTime.value.toString()) // Save time
                        navController.navigate(GAME_END.path)
                    }
                )
            }
            // Display Timer and Hit Count outside the pause menu conditional
            // Ensure these are only shown when the game is not paused
            else
            {
                Spacer(modifier = Modifier.height(10.dp))

                val density = LocalDensity.current
                val boxSize = with(density) { gameViewModel.objectSizeAndViewManager.GET_OBJECT_SIZE().toFloat().toDp() }
                val iconScale = 0.5f
                val enemyIconScale = 0.7f

                val gameFontFamily = FontFamily(
                    Font(R.font.gontdiner_swanky_regular, FontWeight.Normal),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.33f)
                        .padding(start = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        Image(painter = painterResource(id = R.drawable.heart),
                                contentDescription = "heart",
                                modifier = Modifier.size(boxSize * iconScale))
                        Text(
                            text = "x ${gameViewModel.hero.getLives()}",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontFamily = gameFontFamily,
                            modifier = Modifier.padding(2.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.33f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically)
                    {

                        Text(
                            text = "${gameTime.value}s",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontFamily = gameFontFamily,
                            modifier = Modifier.padding(2.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                        .weight(0.33f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        Column(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center)
                        {
                            var hasEnemy = false
                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically)
                            {
                                gameViewModel.currentEnemyList.forEach{ enemyConfig->
                                    gameViewModel.gameMetricsAndCtrl.getEnemyTypeImage(enemyConfig.eType) ?. let{ it
                                        Image(painter = BitmapPainter(it),
                                            contentDescription = enemyConfig.toString(),
                                            modifier = Modifier.size(boxSize * enemyIconScale))

                                        hasEnemy = true
                                    }
                                }
                            }

                            if (hasEnemy)
                            {
                                Text(
                                    text = "x ${gameViewModel.gameMetricsAndCtrl.getEnemyRemain()}",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontFamily = gameFontFamily,
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                }
            }
        }
    }


}