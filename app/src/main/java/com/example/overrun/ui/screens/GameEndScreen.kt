package com.example.overrun.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.MAIN_MENU
import com.example.overrun.enitities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GameEndScreen(navController: NavController, gameViewModel: GameViewModel) {
    val context = LocalContext.current // Get Context
    val timeSurvived = gameViewModel.gameMetricsAndCtrl.getTimeSurvived()
    val eliminations = gameViewModel.gameMetricsAndCtrl.getEnemyKillCount()
    val currentMap = gameViewModel.getCurrentMap() // Ensure you get the map name

    // Animate vertical bounce
    val infiniteTransition = rememberInfiniteTransition()
    val bounceYOffset by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f, // move up down
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val bounceXOffset by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f, // move left right
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundRes = if (gameViewModel.gameMetricsAndCtrl.isStageClear()) {
            R.drawable.escape_background // victory background
        } else {
            R.drawable.banished_background    // game over background
        }

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier.fillMaxSize().padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(color = Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val gameFontFamily = FontFamily(
                        Font(R.font.atma_semibold, FontWeight.Normal),
                    )

                    if (gameViewModel.gameMetricsAndCtrl.isStageClear())
                    {
                        Text("Stage Clear!",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFFE79619),
                            fontFamily = gameFontFamily,
                            modifier = Modifier.offset(
                                x = bounceXOffset.dp,
                                y = bounceYOffset.dp
                            )
                        )
                    }
                    else {
                        Text("Game Over!",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFFE79619),
                            fontFamily = gameFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Time Survived: $timeSurvived secs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text("Eliminations: $eliminations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate(MAIN_MENU.path) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            // containerColor = Color(0xFFFF9800), // orange background
                            contentColor = Color.White          // white text
                        )
                    ) {
                        Text("Back to Main Menu")
                    }

                    Button(
                        onClick = { addStatsToLeaderboard(context, gameViewModel, currentMap) }, // Pass context
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            // containerColor = Color(0xFFFF9800), // orange background
                            contentColor = Color.White          // white text
                        )
                    ) {
                        Text("Add Stats to Leaderboard")
                    }
                }
            }
        }
    }
}
fun addStatsToLeaderboard(context: Context, gameViewModel: GameViewModel, rawMapName: String) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Toast.makeText(context, "You need to be logged in!", Toast.LENGTH_SHORT).show()
        return
    }

    // Sanitize the map name to be Firestore-safe (remove slashes, dots, etc.)
    val mapName = rawMapName.replace(Regex("[^A-Za-z0-9_-]"), "_")

    val timeSurvived = gameViewModel.gameMetricsAndCtrl.getTimeSurvived()
    val eliminations = gameViewModel.gameMetricsAndCtrl.getEnemyKillCount()

    // Fetch username from Firestore (ensure the username exists in the user's document)
    db.collection("users").document(user.uid).get()
        .addOnSuccessListener { document ->
            val username = document.getString("username") ?: "Unknown Player"

            val playerStats = hashMapOf(
                "userId" to user.uid,
                "username" to username,  // Now we are using Firestore's stored username
                "mapName" to mapName,
                "timeSurvived" to timeSurvived.toInt(),
                "eliminations" to eliminations.toInt(),
                "timestamp" to System.currentTimeMillis()
            )

            // Use a flat collection structure for simplicity (you can later index by mapName)
            db.collection("leaderboard")
                .add(playerStats)
                .addOnSuccessListener {
                    Toast.makeText(context, "Stats added successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to add stats", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Failed to fetch username", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
}
