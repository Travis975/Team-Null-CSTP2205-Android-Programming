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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.overrun.R

@Composable
fun CharacterIcon(
    characterId: Int,
    characterName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(108.dp) // Size for the icon container
            .background(
                color = if (isSelected) Color.Cyan.copy(alpha = 0.5f) else Color.Transparent,
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
                    id = when (characterName) {
                        // "Paul" uses the original green Tokage's attacking sprite
                        "Paul" -> R.drawable.tokage_right_hit

                        // "Jacob" uses the orange version (example: tokage_right_hit_orange)
                        "Jacob" -> R.drawable.tokage_right_hit_orange

                        // For unknown or locked characters, fall back to an unavailable icon
                        "????" -> R.drawable.unavailable_icon
                        "???" -> R.drawable.unavailable_icon

                        else -> R.drawable.unavailable_icon
                    }
                ),
                contentDescription = "$characterName Icon",
                modifier = Modifier.size(64.dp), // Fixed size for the icons
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = characterName,
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
