package com.example.overrun.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.MAIN_MENU
import com.example.overrun.enitities.eHeroType
import com.example.overrun.ui.components.CharacterIcon
import com.example.overrun.ui.components.LevelIcon

@Composable
fun StartGameScreen(navController: NavController, gameViewModel: GameViewModel) {
    var selectedCharacter by remember { mutableStateOf<Int?>(null) }
    var selectedLevel by remember { mutableStateOf<Int?>(null) }

    var is100HealthDrop by remember{gameViewModel.is100PercentHealthDrop}

    LaunchedEffect(Unit)
    {
        gameViewModel.set100PercentHealthDrop(false)
    }

    // Updated characterNames map:
    // 1 -> "Paul"
    // 2 -> "Jacob"
    val characterNames = mapOf(
        1 to "Paul",
        2 to "Jacob",
        3 to "Max"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.grass_dungeon),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Add a glitch button to enable 100 % health drop by enemy
            Checkbox(
                checked = is100HealthDrop,
                onCheckedChange = {gameViewModel.set100PercentHealthDrop(!is100HealthDrop)},
                modifier = Modifier
                    .alpha(if (is100HealthDrop) 1f else 0.1f)
                    .size(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Changed to fillMaxWidth() to ensure enough space for the row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Select Character",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Character Selection Icons
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CharacterIcon(
                            characterId = 1,
                            isSelected = selectedCharacter == 1,
                            onClick = {
                                selectedCharacter = if (selectedCharacter == 1) null else 1
                            },
                            characterName = characterNames[1] ?: "Unknown"
                        )
                        CharacterIcon(
                            characterId = 2,
                            isSelected = selectedCharacter == 2,
                            onClick = {
                                selectedCharacter = if (selectedCharacter == 2) null else 2
                            },
                            characterName = characterNames[2] ?: "Unknown"
                        )
                        CharacterIcon(
                            characterId = 3,
                            isSelected = selectedCharacter == 3,
                            onClick = {
                                selectedCharacter = if (selectedCharacter == 3) null else 3
                            },
                            characterName = characterNames[3] ?: "Unknown"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Select Level",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Level Selection Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LevelIcon(
                            level = 1,
                            isSelected = selectedLevel == 1,
                            onClick = { selectedLevel = if (selectedLevel == 1) null else 1 }
                        )
                        LevelIcon(
                            level = 2,
                            isSelected = selectedLevel == 2,
                            onClick = { selectedLevel = if (selectedLevel == 2) null else 2 }
                        )
                        LevelIcon(
                            level = 3,
                            isSelected = selectedLevel == 3,
                            onClick = { selectedLevel = if (selectedLevel == 3) null else 3 }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Start Game Button (Enabled only if a level and character are selected)
                    Button(
                        onClick = {
                            if (selectedCharacter != null && selectedLevel != null) {

                                // (1) Set the hero type based on the selected character
                                when (selectedCharacter) {
                                    1 -> {
                                        // Paul
                                        gameViewModel.hero.setHeroType(eHeroType.eHERO_TOKAGE)
                                    }
                                    2 -> {
                                        // Jacob
                                        gameViewModel.hero.setHeroType(eHeroType.eHERO_TOKAGE_ORANGE)
                                    }
                                    3 -> {
                                        // Jacob
                                        gameViewModel.hero.setHeroType(eHeroType.eHERO_TOKAGE_YELLOW)
                                    }
                                }

                                // Assign readable map names instead of just numbers
                                val mapName = when (selectedLevel) {
                                    1 -> "Level 1"
                                    2 -> "Level 2"
                                    3 -> "Level 3"
                                    else -> "Unknown Map"
                                }

                                // Set the selected map in the GameViewModel
                                gameViewModel.setCurrentMap(mapName)

                                // Navigate to the correct level
                                val route = when (selectedLevel) {
                                    1 -> "level1"
                                    2 -> "level2"
                                    3 -> "level3"
                                    else -> MAIN_MENU.path
                                }

                                gameViewModel.triggerStageStartRender()
                                navController.navigate(route)
                            }
                        },
                        enabled = selectedLevel != null && selectedCharacter != null,
                        modifier = Modifier.fillMaxWidth(0.6f),
                        colors = ButtonDefaults.buttonColors(
                            // containerColor = Color(0xFFFF9800), // orange background
                            contentColor = Color.White          // white text
                        )
                    ) {
                        Text("Start Game")
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun StartGamePreview() {
//    val gameViewModel: GameViewModel = viewModel()
//    StartGameScreen(navController = rememberNavController(), gameViewModel)
//}
