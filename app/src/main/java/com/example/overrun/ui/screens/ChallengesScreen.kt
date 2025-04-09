package com.example.overrun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.overrun.R
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.Route.MAIN_MENU

@Composable
fun ChallengesScreen(navController: NavController, gameViewModel: GameViewModel) {
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
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)  // 80% width of the screen
                    .background(
                        color = Color.White.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp)  // Rounded corners
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Trials of Overrun:\n",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Character Challenges:\n",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )


                    ChallengeIcon(
                        challengeText = "1. Get 5 eliminations in one game",
                        isCompleted = (gameViewModel.levelChallengesCompleted["level2"] ?: false) as Boolean,
                        onToggle = { completed ->
                            gameViewModel.completeChallenge(level = "level2", userId = "user123")
                        }
                    )

                    ChallengeIcon(
                        challengeText = "2. Get 10 eliminations in one game",
                        isCompleted = gameViewModel.levelChallengesCompleted["level3"] ?: false,
                        onToggle = { completed ->
                            gameViewModel.completeChallenge(level = "level3", userId = "user123")
                        }
                    )



                    Text(
                        text = "Level Challenges:\n",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    ChallengeIcon(
                        challengeText = "1. Survive for 15 seconds",
                        isCompleted = gameViewModel.characterChallengesCompleted["character2"] ?: false,
                        onToggle = { completed ->
                            gameViewModel.completeChallenge(character = "character2", userId = "user123")
                        }
                    )

                    ChallengeIcon(
                        challengeText = "2. Survive for 30 seconds",
                        isCompleted = gameViewModel.characterChallengesCompleted["character3"] ?: false,
                        onToggle = { completed ->
                            gameViewModel.completeChallenge(character = "character3", userId = "user123")
                        }
                    )

                }
            }

            Spacer(modifier = Modifier.height(96.dp))

            Button(
                onClick = { navController.navigate(MAIN_MENU.path) },
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun ChallengeIcon(challengeText: String, isCompleted: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                onToggle(!isCompleted)
            }
        ) {
            val iconRes = if (isCompleted) {
                R.drawable.checkbox_fill // icon for completed challenge
            } else {
                R.drawable.checkbox_blank // icon for not completed
            }

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Challenge Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        Text(
            text = challengeText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
