package com.example.overrun.enitities.gameobject

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.gohero.enitities.eObjectType
import com.example.overrun.R
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.collider.ColliderManager
import kotlinx.coroutines.delay

@Composable
fun ObjectCompose(
    gameObject: GameObject,
    colliderManager: ColliderManager,
    objectSizeAndViewManager : GameObjectSizeAndViewManager
) {
    // Use xPos and yPos for rendering
    val xPos = remember { Animatable(gameObject.getXPos().toFloat()) }
    val yPos = remember { Animatable(gameObject.getYPos().toFloat()) }

    val density = LocalDensity.current

    val boxSize = with(density) { objectSizeAndViewManager.GET_OBJECT_SIZE().toFloat().toDp() }

    var lastColor by remember{ mutableStateOf(Color.DarkGray)}

    var filterOpacity by remember{mutableStateOf(0f)}

    // snapShotMap stored the interaction timestamp in ms
    val isBeingInteracted = remember(gameObject.getID()) {
        derivedStateOf {
            colliderManager.heroInteractedToOther[gameObject.getID()] ?: 0L     // if no id registered, response as 0L
        }
    }

    // Launch a effect to process the interaction object reaction
    LaunchedEffect(isBeingInteracted.value) {
        // only process when triggered with timestamp recorded
        if (isBeingInteracted.value > 0L) {

            // Can apply for Object to debug for interaction
            lastColor = if (lastColor == Color.DarkGray) Color.Blue else Color.DarkGray

            // under different object configuration to set the object data
            // TO DO
            // Set Destroy, Active or InActive
            if (gameObject.getCollider().isInteractable()) // if the object is interactable
            {
                filterOpacity = 0.8f
            }
        }
    }

    LaunchedEffect(filterOpacity){
        if (filterOpacity > 0f)
        {
            delay(50)
            filterOpacity -= 0.3f
        }
        else
        {
            filterOpacity = 0f
        }
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.TopStart)
                .absoluteOffset { IntOffset(xPos.value.toInt(), yPos.value.toInt()) }
                .background(Color.Transparent)
        ) {
            when (gameObject.getObjType()) {

                eObjectType.eGRASS -> {
                    // Grass tile
                    // ContentScale.FillBounds ensures no extra borders or seams
                    Image(
                        painter = painterResource(id = R.drawable.grass_tile),
                        contentDescription = "Grass tile",
                        modifier = Modifier.fillMaxSize(),
                        //contentScale = ContentScale.FillBounds
                        contentScale = ContentScale.Fit
                    )
                }

                eObjectType.eROCK -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(lastColor)
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.rock1_1),
                        contentDescription = "rock1_1",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = filterOpacity), BlendMode.SrcAtop)
                    )
                }

                eObjectType.eTREE -> {
                    Image(
                        painter = painterResource(id = R.drawable.tree_1),
                        contentDescription = "tree tile",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
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
