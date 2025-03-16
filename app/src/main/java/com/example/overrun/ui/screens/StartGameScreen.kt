package com.example.overrun.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.overrun.enitities.Route.*

@Composable
fun StartGameScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Select level")
//        Button(onClick = { navController.navigate(LEVEL_1.path) }) {
//            Text("Start Level 1")
//        }
        // Temporary Added to test character
        Button(
            onClick = { navController.navigate(TEST_CHARACTER.path) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Go to Test Character")
        }
    }
}


// Preview without NavController
@Preview(showBackground = true)
@Composable
fun StartGamePreview() {
    StartGameScreen(navController = rememberNavController())
}
