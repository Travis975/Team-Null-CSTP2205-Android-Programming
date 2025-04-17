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
fun PrivacyScreen(navController: NavController) {
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
                        text = "Privacy Policy\n",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Welcome to Overrun! Your privacy is important to us. This Privacy Policy outlines how we collect, use, and protect your personal information when you play our game. By accessing or using Overrun, you agree to the terms outlined in this policy.\n\n" +
                                "1. Information We Collect\n" +
                                "- Account Data: If you sign in using Firebase authentication (Google, Discord, or other OAuth providers), we collect your email and username to manage your profile and in-game progress.\n" +
                                "- Game Progress: We store your in-game progress, scores, achievements, and leaderboard rankings.\n" +
                                "- Device Information: We may collect data about your device (OS version, device model) to optimize performance.\n" +
                                "- Analytics Data: We may use analytics tools to track how players interact with Overrun to enhance game features.\n\n" +
                                "2. How We Use Your Information\n" +
                                "- To provide and improve the game experience.\n" +
                                "- To store and sync your game progress.\n" +
                                "- To enable leaderboards and multiplayer features.\n" +
                                "- To enhance security, detect fraud, and prevent cheating.\n\n" +
                                "3. Data Security\n" +
                                "We take reasonable security measures to protect your data from unauthorized access, alteration, or loss. However, no digital system is 100% secure.\n\n" +
                                "4. Your Rights & Choices\n" +
                                "- You can request access to your data or request its deletion by contacting us.\n" +
                                "- You can disable certain analytics tracking in the game settings.\n" +
                                "- You can unlink third-party accounts from your profile settings.\n\n" +
                                "5. Children’s Privacy\n" +
                                "Overrun is not intended for children under the age of 13. We do not knowingly collect data from minors without parental consent.\n\n" +
                                "6. Contact Us\n" +
                                "If you have any questions or concerns about this Privacy Policy, contact us at: help@overrun.com"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(SIGNUP.path) },
                modifier = Modifier
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // orange background
                    contentColor = Color.White          // white text
                )
            ) {
                Text("Back")
            }
        }
    }
}
