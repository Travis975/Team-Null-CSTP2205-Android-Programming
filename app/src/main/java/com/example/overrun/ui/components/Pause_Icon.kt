package com.example.overrun.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.example.overrun.enitities.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PauseIcon(navController: NavController, gameViewModel: GameViewModel = viewModel(), onPause: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Pause,
        contentDescription = "Pause",
        tint = Color.Black,
        modifier = Modifier.clickable {
            // Trigger the pause action
            onPause()
        }
    )
}
