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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.overrun.ui.components.CharacterIcon
import com.example.overrun.ui.components.LevelIcon
@Composable
fun StartGameScreen(navController: NavController) {
    var selectedCharacter by remember { mutableStateOf<Int?>(null) }
    var selectedLevel by remember { mutableStateOf<Int?>(null) }

    val characterNames = mapOf(
        1 to "Paul",
        2 to "????"
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
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
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
                            onClick = { selectedCharacter = if (selectedCharacter == 1) null else 1 },
                            characterName = characterNames[1] ?: "Unknown"
                        )
                        CharacterIcon(
                            characterId = 2,
                            isSelected = selectedCharacter == 2,
                            onClick = { selectedCharacter = if (selectedCharacter == 2) null else 2 },
                            characterName = characterNames[2] ?: "Unknown"
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
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Start Game Button (Enabled only if a level and character are selected)
                    Button(
                        onClick = {
                            val route = when {
                                selectedCharacter != null && selectedLevel != null -> {
                                    // Navigate to the corresponding level screen based on the selected level
                                    when (selectedLevel) {
                                        1 -> "level1"
                                        2 -> "level2"
                                        else -> MAIN_MENU.path
                                    }
                                }
                                else -> MAIN_MENU.path
                            }
                            navController.navigate(route)
                        },
                        enabled = selectedLevel != null && selectedCharacter != null,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Start Game")
                    }
                }
            }


        }
    }
}



// Preview
@Preview(showBackground = true)
@Composable
fun StartGamePreview() {
    StartGameScreen(navController = rememberNavController())
}
