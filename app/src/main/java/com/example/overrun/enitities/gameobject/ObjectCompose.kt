package com.example.overrun.enitities.gameobject

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.gohero.enitities.GameConstant.DEFAULT_OBJECT_SIZE
import com.example.gohero.enitities.eObjectType
import com.example.overrun.R
import com.example.overrun.enitities.collider.ColliderManager

@Composable
fun ObjectCompose(
    gameObject: GameObject,
    colliderManager: ColliderManager
) {
    // Use xPos and yPos for rendering
    val xPos = remember { Animatable(gameObject.getXPos().toFloat()) }
    val yPos = remember { Animatable(gameObject.getYPos().toFloat()) }

    val density = LocalDensity.current

    // Normal collision check, but grass won't be included anyway
    val isColliding = remember {
        derivedStateOf {
            colliderManager.getHeroCollider()?.IsCollided(gameObject.getCollider()) ?: false
        }
    }

    val boxSize = with(density) { DEFAULT_OBJECT_SIZE.toFloat().toDp() }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.TopStart)
                .absoluteOffset { IntOffset(xPos.value.toInt(), yPos.value.toInt()) }
        ) {
            when (gameObject.getObjType()) {

                eObjectType.eGRASS -> {
                    // Grass tile
                    // ContentScale.FillBounds ensures no extra borders or seams
                    Image(
                        painter = painterResource(id = R.drawable.grass_tile),
                        contentDescription = "Grass tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                eObjectType.eROCK -> {
                    // If you have a rock image, replace with an Image(...) here.
                    // For now, let's just draw a gray box:
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                    )
                }

                eObjectType.eTREE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.tree_w_grass_1),
                            contentDescription = "tree tile",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> {
                    // Default magenta box for other object types
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Magenta)
                    )
                }
            }
        }
    }
}
