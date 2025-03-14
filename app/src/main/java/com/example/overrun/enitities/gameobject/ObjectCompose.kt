package com.example.overrun.enitities.gameobject

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.example.overrun.enitities.eObjectType.*
import com.example.overrun.R
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.sprites.loadSpriteSheet
import kotlinx.coroutines.delay

@Composable
fun ObjectCompose(
    gameObject: GameObject,
    colliderManager: ColliderManager,
    objectSizeAndViewManager : GameObjectSizeAndViewManager
) {
    val context = LocalContext.current

    // Create remember BitmapResource to store cache to prevent recreation from every rendering
    // !!!! It is important to improve the render efficiency
    val objectbitmapPainter = remember(gameObject.getObjType()){
        when(gameObject.getObjType())
        {
            eGRASS->BitmapPainter(loadSpriteSheet(context.resources, R.drawable.grass_tile))
            eTREE, eTREE_BACKGROUND->BitmapPainter(loadSpriteSheet(context.resources, R.drawable.tree_1))
            eROCK, eROCK_1, eROCK_TOXIC->BitmapPainter(loadSpriteSheet(context.resources, R.drawable.rock1_1))
            else->BitmapPainter(loadSpriteSheet(context.resources, R.drawable.grass_tile))
        }
    }

    // For not within the Screen, not to render
    if (!objectSizeAndViewManager.IsObjectInScreen(gameObject.getCollider()))
    {
        return
    }

    // Use xPos and yPos for rendering
    //val xPos = remember { Animatable(gameObject.getXPos().toFloat()) }
    //val yPos = remember { Animatable(gameObject.getYPos().toFloat()) }

    // Change to use World Coord for Screen X Y Pos system for trigger rendering
    val xScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldX)
    val yScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldY)




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

    //Log.i("Object", "Type ${gameObject.getObjType()}  id : ${gameObject.getID()} x : ${gameObject.getXPos()}  y : ${gameObject.getYPos()}")

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.TopStart)
                // don't use graphicLayer since its transformation would auto scaling and translate
                // may cause edge residue issue for the rendering
//                .graphicsLayer {
//                    translationX = (gameObject.getXPos().toFloat() - xScreenPos)
//                    translationY = (gameObject.getYPos().toFloat() - yScreenPos)
//                }
                .absoluteOffset {
                    IntOffset(
                        gameObject.getXPos().toInt() - xScreenPos.toInt(),
                        gameObject.getYPos().toInt() - yScreenPos.toInt())
                }
                .background(Color.Transparent)
        ) {
            when (gameObject.getObjType()) {

                eGRASS -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Grass tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                eROCK, eROCK_1, eROCK_TOXIC -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "rock1_1",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = filterOpacity), BlendMode.SrcAtop)
                    )
                }

                eTREE, eTREE_BACKGROUND -> {
                    Image(
                        painter = objectbitmapPainter,
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
