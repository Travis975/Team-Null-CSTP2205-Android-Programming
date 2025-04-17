package com.example.overrun.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsIcon(
    navController: NavController,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Settings"
) {
    Icon(
        imageVector = Icons.Filled.Settings,
        contentDescription = contentDescription,
        modifier = modifier.size(42.dp).clickable {
            // Navigate to the settings screen
            navController.navigate("settings")
        },
        tint = Color(0xFFFFA500) // Orange

    )
}
