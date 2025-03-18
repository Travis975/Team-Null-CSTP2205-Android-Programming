package com.example.overrun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.overrun.R

@Composable
fun LevelIcon(level: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(128.dp) // Size for the icon container
            .background(
                color = if (isSelected) Color.Yellow.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(
                    id = when (level) {
                        1 -> R.drawable.level_1_icon
                        2 -> R.drawable.level_2_icon
                        else -> R.drawable.unavailable_icon
                    }
                ),
                contentDescription = "Level $level",
                modifier = Modifier
                    .size(96.dp), // Fixed size for the icons
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Level $level",
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }
}
