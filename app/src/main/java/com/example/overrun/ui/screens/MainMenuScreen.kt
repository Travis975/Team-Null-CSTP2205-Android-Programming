package com.example.overrun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.R
import com.example.overrun.enitities.Route.*

@Composable
fun MainMenuScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.forest_frawing),
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

            Image(
                painter = painterResource(id = R.drawable.main_menu_text),
                contentDescription = "Main Menu Text",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(96.dp))

            Button(
                onClick = { navController.navigate(START_GAME.path) },
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {
                Text("Start Game")
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { navController.navigate(CONTROLS.path) },
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {
                Text("Controls")
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { navController.navigate(HOME.path) },
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {
                Text("Quit")
            }
        }
    }
}


// Preview without NavController
@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MainMenuScreen(navController = rememberNavController())
}
