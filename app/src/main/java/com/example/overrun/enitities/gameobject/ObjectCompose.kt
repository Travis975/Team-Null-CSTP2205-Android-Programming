package com.example.overrun.enitities.gameobject

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.gohero.enitities.GameConstant.DEFAULT_OBJECT_SIZE
import com.example.overrun.enitities.collider.ColliderManager

@Composable
fun ObjectCompose(gameObject: GameObject,
                  colliderManager: ColliderManager)
{
    // use xPos and yPos for rendering
    val xPos = remember { Animatable(gameObject.getXPos().toFloat()) }
    val yPos = remember { Animatable(gameObject.getYPos().toFloat()) }

    val density = LocalDensity.current

    //Log.i("Object Pos", "(${xPos.value}, ${yPos.value})")

    // Setup event trigger to re-render if this object collided with the hero collider
    val isColliding = remember {
        derivedStateOf { colliderManager.getHeroCollider()?.IsCollided(gameObject.getCollider()) }
    }

    val boxSize = with(density){ DEFAULT_OBJECT_SIZE.toFloat().toDp() }

    Box(Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            // Assign the Size of Pixel corresponding dp to create the box
            .size(boxSize)
            .align(Alignment.TopStart)
            // Offset is in Pixel
            .absoluteOffset { IntOffset(xPos.value.toInt(), yPos.value.toInt()) }
            .background(Color.Magenta),
            contentAlignment = Alignment.Center)
        {

        }
    }
}