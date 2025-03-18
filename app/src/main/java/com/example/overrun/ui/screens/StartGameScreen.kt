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
import com.example.overrun.ui.components.LevelIcon


@Composable
fun StartGameScreen(navController: NavController) {
    var selectedLevel by remember { mutableStateOf<Int?>(null) }

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
                    .padding(50.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Select Level",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                        // Commenting out Level 3 for now
                        // LevelIcon(
                        //     level = 3,
                        //     isSelected = selectedLevel == 3,
                        //     onClick = { selectedLevel = if (selectedLevel == 3) null else 3 }
                        // )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Game Button (Enabled only if a level is selected)
            Button(
                onClick = {
                    val route = when (selectedLevel) {
                        1 -> LEVEL_1
                        2 -> LEVEL_2
                        // Commenting out Level 3 for now
                        // 3 -> LEVEL_3
                        else -> MAIN_MENU
                    }
                    navController.navigate(route.path)
                },
                enabled = selectedLevel != null,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Start Game")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate(MAIN_MENU.path) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Back")
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
