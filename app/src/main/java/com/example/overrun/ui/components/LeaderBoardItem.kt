package com.example.overrun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.overrun.enitities.LeaderboardEntry


@Composable
fun LeaderboardItem(stat: LeaderboardEntry, rank: Int) {

    // Set colours for the top 3 spots of the leaderboard
    val backgroundColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold for 1st place
        2 -> Color(0xFFB8B8B8) // Silver for 2nd place
        3 -> Color(0xFFCD7F32) // Bronze for 3rd place
        else -> Color.White.copy(alpha = 0.8f) // Default color for others
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text("Username: ${stat.username}", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text("Time Survived: ${stat.timeSurvived}s", fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.Black)
            Text("Eliminations: ${stat.eliminations}", fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.Black)
        }
    }
}
