package com.example.overrun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.*

@Composable
fun ControlsScreen(navController: NavController) {
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
                        text = "Game Controls:\n",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Movement:\n- Hold and drag your thumb in the direction you want to go.\n\n" +
                                "Attack:\n- Tap anywhere on the screen to attack in the direction your character is facing.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
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


// Preview
@Preview(showBackground = true)
@Composable
fun ControlsPreview() {
    ControlsScreen(navController = rememberNavController())
}
