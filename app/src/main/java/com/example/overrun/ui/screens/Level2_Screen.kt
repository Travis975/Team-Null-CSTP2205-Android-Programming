package com.example.overrun.ui.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.enitities.character.HeroCompose
import com.example.overrun.enitities.eGameStage
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.MAIN_MENU
import com.example.overrun.enitities.gameStage.GameStageManager
import com.example.overrun.enitities.gameobject.ObjectCompose
import com.example.overrun.ui.components.PauseIcon
import com.example.overrun.ui.components.PauseMenu
import kotlinx.coroutines.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Level2_Screen(navController: NavController, gameViewModel: GameViewModel) {

    // Use Remember to ensure only one instance for this Game Stage Screen
    val gameStageManager : GameStageManager = remember{ GameStageManager(eGameStage.eStage1) }

    val density = LocalDensity.current
    val context = LocalContext.current

    val isGameStageInitialized = remember { mutableStateOf(false) }

    // Starting enemy update coroutine
    LaunchedEffect(Unit) {
        gameViewModel.startEnemyUpdates() // Start when game launches
    }

    // Timer state
    val isTimerRunning = gameViewModel.isTimerRunning.value

    val gameTimeLevel2 = remember { mutableStateOf(0) } // Tracks elapsed seconds

    // Timer logic with pause-resume control
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (true) {
                delay(1000L) // Wait 1 second
                gameTimeLevel2.value++ // Increment timer
            }
        }
    }

    // State to control the visibility of the pause menu
    val isPauseDialogVisible = remember { mutableStateOf(false) }

    // Call Once when the Screen first Compose
    DisposableEffect(Unit) {
        println("Screen has entered the composition")

        // Cleanup when the screen is navigated away from
        onDispose {

            // To Do
            // Clean up memory or stop the coroutine that created for gaming
            // Important !! //
            gameViewModel.colliderManager.cancelCollisionCheck()

            println("Game Screen is leaving the composition")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {

        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            , contentAlignment = Alignment.TopStart)
        {
            // Only Run Once when start
            LaunchedEffect(Unit) {

                val screenWidthPx = with(density){maxWidth.toPx()}
                val screenHeightPx = with(density){maxHeight.toPx()}

                Log.i("screen", "widthPx : $screenWidthPx, heightPx : $screenHeightPx")

                gameStageManager.InitGameStage(context = context,
                                                gameVM = gameViewModel,
                                                screenWidth= screenWidthPx.toUInt(),
                                                screenHeight = screenHeightPx.toUInt())

                isGameStageInitialized.value = true
            }

            if (isGameStageInitialized.value)
            {
                gameViewModel.gameObjects.forEach{ gameObj ->
                    ObjectCompose(gameObj,
                                  gameViewModel.gameMetrics,
                                  gameViewModel.colliderManager,
                                  gameViewModel.objectSizeAndViewManager)
                }

                // And Enemy here shall overlap the hero

                // Remember, hero always overlap the game object
                HeroCompose(gameViewModel.hero,
                            gameViewModel.colliderManager,
                            gameViewModel.objectSizeAndViewManager)



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
                                gameViewModel.toggleTimer() // Stop the timer on pause
                            }
                        )

                        // Pause Menu Dialog
                        if (isPauseDialogVisible.value) {
                            PauseMenu(
                                onResume = {
                                    isPauseDialogVisible.value = false // Close the dialog
                                    gameViewModel.toggleTimer() // Resume the timer
                                },
                                onQuit = {
                                    navController.navigate(MAIN_MENU.path) // Navigate to the main menu
                                }
                            )
                        }

                        // Display Timer and Hit Count outside the pause menu conditional
                        if (!isPauseDialogVisible.value) {  // Ensure these are only shown when the game is not paused
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Time: ${gameTimeLevel2.value}s",
                                    color = Color.Black,
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Hit Count: ${gameViewModel.gameMetrics.getHeroHitCount()}",
                                    color = Color.Black,
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

@Preview(showBackground = true)
@Composable
fun Preview() {
    val gameViewModel: GameViewModel = viewModel()
    Level2_Screen(navController = rememberNavController(), gameViewModel)
}