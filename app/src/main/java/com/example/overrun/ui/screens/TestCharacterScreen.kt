package com.example.overrun.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gohero.enitities.character.HeroCompose
import com.example.gohero.enitities.eGameStage
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.HOME
import com.example.overrun.enitities.Route.MAIN_MENU
import com.example.overrun.enitities.gameStage.GameStageManager
import com.example.overrun.enitities.gameobject.ObjectCompose

@Composable
fun TestCharacterGameScreen(navController: NavController,
                            gameViewModel: GameViewModel)
{
    // Use Remember to ensure only one instance for this Game Stage Screen
    val gameStageManager : GameStageManager = remember{ GameStageManager(eGameStage.eStage1) }

    val density = LocalDensity.current

    val isGameStageInitialized = remember { mutableStateOf(false) }

    // Call Once when the Screen first Compose
    DisposableEffect(Unit) {
        println("Screen has entered the composition")

        // Cleanup when the screen is navigated away from
        onDispose {

            // To Do
            // Clean up memory or stop the coroutine that created for gaming
            // Important !! //
            gameViewModel.colliderManager.cancelHeroActionCollisionCheck()

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
        Button(
            onClick = { navController.navigate(MAIN_MENU.path) },
            modifier = Modifier
                .padding(bottom = 12.dp)
        ) {
            Text("Quit")
        }

        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            , contentAlignment = Alignment.Center)
        {
            // Only Run Once when start
            LaunchedEffect(Unit) {

                val screenWidthPx = with(density){maxWidth.toPx()}
                val screenHeightPx = with(density){maxHeight.toPx()}

                Log.i("screen", "widthPx : $screenWidthPx, heightPx : $screenHeightPx")

                gameStageManager.InitGameStage(gameVM = gameViewModel,
                                                screenWidth= screenWidthPx.toUInt(),
                                                screenHeight = screenHeightPx.toUInt())

                isGameStageInitialized.value = true
            }

            if (isGameStageInitialized.value)
            {
                gameViewModel.gameObjects.forEach{ gameObj ->
                    ObjectCompose(gameObj,
                                  gameViewModel.colliderManager,
                                  gameViewModel.objectSizeManager)
                }

                // And Enemy here shall overlap the hero

                // Remember, hero always overlap the game object
                HeroCompose(gameViewModel.hero,
                            gameViewModel.colliderManager,
                            gameViewModel.objectSizeManager)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val gameViewModel: GameViewModel = viewModel()
    TestCharacterGameScreen(navController = rememberNavController(),
                            gameViewModel)
}