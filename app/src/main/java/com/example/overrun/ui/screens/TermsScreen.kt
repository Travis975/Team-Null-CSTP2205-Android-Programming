package com.example.overrun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.overrun.enitities.Route.SIGNUP

@Composable
fun TermsScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.forest_cartoon),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)  // 80% width of the screen
                    .background(
                        color = Color.White.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp)  // Rounded corners
                    )
                    .padding(16.dp)
                    .weight(1f) // Allows the box to take up remaining space
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(
                        text = "Terms and Conditions\n",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Welcome to Overrun! These Terms and Conditions govern your use of the game. By accessing or playing Overrun, you agree to abide by these terms.\n\n" +
                                "1. User Conduct\n" +
                                "- You agree to play fairly and not exploit bugs, hacks, or cheats.\n" +
                                "- You will not engage in harassment, hate speech, or any behavior that disrupts the community.\n\n" +
                                "2. Account and Data\n" +
                                "- You are responsible for keeping your login credentials secure.\n" +
                                "- We may collect and store game-related data, such as your progress and leaderboard rankings.\n\n" +
                                "3. Virtual Goods and In-Game Purchases\n" +
                                "- Any in-game purchases are non-refundable.\n" +
                                "- We reserve the right to modify virtual items, including their availability and attributes.\n\n" +
                                "4. Updates and Changes\n" +
                                "- We may update Overrun at any time, which could impact gameplay, progress, or in-game items.\n" +
                                "- Continued use after an update constitutes acceptance of new changes.\n\n" +
                                "5. Liability\n" +
                                "- We are not responsible for data loss, crashes, or any issues arising from third-party services.\n" +
                                "- Overrun is provided \"as is,\" and we make no guarantees regarding uptime or functionality.\n\n" +
                                "6. Termination\n" +
                                "- We reserve the right to terminate accounts that violate these terms.\n" +
                                "- Any misuse of the game may result in account suspension or banning.\n\n" +
                                "7. Contact\n" +
                                "If you have any questions or concerns about these terms, contact us at: help@overrun.com"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(SIGNUP.path) },
                modifier = Modifier.padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    // containerColor = Color(0xFFFF9800), // orange background
                    contentColor = Color.White          // white text
                )
            ) {
                Text("Back")
            }
        }
    }
}
