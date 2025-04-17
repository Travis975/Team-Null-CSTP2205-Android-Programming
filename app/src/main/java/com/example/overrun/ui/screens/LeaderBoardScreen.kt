package com.example.overrun.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.overrun.enitities.LeaderboardEntry
import com.example.overrun.enitities.Route.MAIN_MENU
import com.example.overrun.ui.components.LeaderboardItem
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LeaderBoardScreen(navController: NavController) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var leaderboardData by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }

    // Levels for which leaderboard data will be displayed
    val levels = listOf("Level 1", "Level 2", "Level 3")

    // Fetch leaderboard data when level is selected
    LaunchedEffect(selectedLevel) {
        if (selectedLevel != null) {
            // Replace spaces with underscores to match Firestore format (Level_1)
            val firestoreLevel = selectedLevel?.replace(" ", "_")
            firestoreLevel?.let {
                fetchLeaderboardData(it) { data ->
                    leaderboardData = data
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.grass_dungeon),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween, // Ensures button stays at the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top container with title, buttons, leaderboard
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take all available vertical space except what bottom button uses
                    .background(
                        color = Color.White.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Leaderboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                levels.forEach { level ->
                    Button(
                        onClick = { selectedLevel = level },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            // containerColor = Color(0xFFFF9800), // orange background
                            contentColor = Color.White          // white text
                        )
                    ) {
                        Text("Show $level Leaderboard")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                selectedLevel?.let {
                    Text("Top 10 for $it", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (leaderboardData.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Scroll within remaining space
                        ) {
                            itemsIndexed(leaderboardData) { index, stat ->
                                Column {
                                    Text(
                                        // add 1 since start at 0
                                        text = "#${index + 1}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3F51B5),
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                                    )
                                    // pass the stats and the rank
                                    LeaderboardItem(stat, rank = index + 1)
                                }
                            }
                        }
                    } else {
                        Text("No data available for $it.", color = Color.Black)
                    }
                }
            }

            Button(
                onClick = { navController.navigate(MAIN_MENU.path) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // orange background
                    contentColor = Color.White          // white text
                )
            ) {
                Text("Back to Main Menu")
            }
        }
    }
}

fun fetchLeaderboardData(level: String, callback: (List<LeaderboardEntry>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("leaderboard")
        .whereEqualTo("mapName", level)  // Now level will be "Level_1", "Level_2", etc.
        .orderBy("timeSurvived", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(10)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val leaderboardList = querySnapshot.documents.map { document ->
                val username = document.getString("username") ?: "Unknown Player"
                val timeSurvived = document.getLong("timeSurvived") ?: 0L
                val eliminations = document.getLong("eliminations") ?: 0L
                LeaderboardEntry(username, timeSurvived, eliminations)
            }
            callback(leaderboardList)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            callback(emptyList())
        }
}

