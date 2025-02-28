package com.example.overrun.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.overrun.enitities.Route.*

@Composable
fun SignInScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign in Screen")
        Button(onClick = { navController.navigate(HOME.path) }) {
            Text("Back to Home")
        }
        Button(onClick = { navController.navigate(MAIN_MENU.path) }) {
            Text("Go to main menu")
        }
    }
}