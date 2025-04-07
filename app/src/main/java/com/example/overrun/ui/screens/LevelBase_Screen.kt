package com.example.overrun.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.character.EnemyCompose
import com.example.overrun.enitities.character.HeroCompose
import com.example.overrun.enitities.eGameStage
import com.example.overrun.enitities.gameStage.GameStageManager
import com.example.overrun.enitities.gameobject.ObjectCompose
import com.example.overrun.ui.components.ScreenControlAndMetrics


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LevelBase_Screen(navController: NavController,
                     gameViewModel: GameViewModel,
                     eStage : eGameStage) {

    // Use Remember to ensure only one instance for this Game Stage Screen
    val gameStageManager : GameStageManager = remember{ GameStageManager(eStage) }

    val density = LocalDensity.current
    val context = LocalContext.current

    val isGameStageInitialized = remember { mutableStateOf(false) }

    // declare for re-render trigger
    val isNewStart = gameViewModel.isStageStartRender

    // Call Once when the Screen first Compose
    DisposableEffect(Unit) {
        println("Screen has entered the composition")

        // Cleanup when the screen is navigated away from
        onDispose {

            // To Do
            // Clean up memory or stop the coroutine that created for gaming
            // Important !! //
            gameViewModel.colliderManager.cancelCollisionCheck()

            gameViewModel.destructEnemyFactoryRoutine()

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

                Log.i("screen", "Stage: $eStage, widthPx : $screenWidthPx, heightPx : $screenHeightPx")

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
                        gameViewModel.gameMetricsAndCtrl,
                        gameViewModel.colliderManager,
                        gameViewModel.objectSizeAndViewManager
                    )
                }

                // Remember, hero always overlap the game object
                HeroCompose(gameViewModel.hero,
                    gameViewModel::setGameObjectDestroyByID,
                    gameViewModel.gameMetricsAndCtrl,
                    gameViewModel.colliderManager,
                    gameViewModel.objectSizeAndViewManager)

                // And Enemy here shall overlap the hero
                gameViewModel.enemies.forEach { enemy ->
                    EnemyCompose(
                        enemy,
                        gameViewModel.hero::getHeroXYPos,
                        gameViewModel::dynamicGameObjectCreation,
                        gameViewModel.gameMetricsAndCtrl,
                        gameViewModel.colliderManager,
                        gameViewModel.objectSizeAndViewManager
                    )
                }

                // Screen Control
                ScreenControlAndMetrics(navController, gameViewModel)
            }
        }
    }
}