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
    objectSizeAndViewManager: GameObjectSizeAndViewManager
) {
    val context = LocalContext.current

    // Create a remembered BitmapPainter to store/cache the tile image
    // so we don't recreate it on every render.
    val objectbitmapPainter = remember(gameObject.getObjType()) {
        // Map each eObjectType to its corresponding drawable resource
        val resourceId = when (gameObject.getObjType()) {
            eGRASS -> R.drawable.grass_tile
            eTREE -> R.drawable.tree_1
            eROCK, eROCK_1 -> R.drawable.rock1_1

            // The newly added object types and their drawables:
            ePATH_BLANK_MUD -> R.drawable.path_blank_mud
            ePATH_LEFT_BOUNDARY -> R.drawable.path_left_boundary
            ePATH_RIGHT_BOUNDARY -> R.drawable.path_right_boundary
            ePATH_RANDOM -> R.drawable.random_path_tile
            ePATH_RANDOM_2 -> R.drawable.random_path_tile_2
            ePATH_RANDOM_3 -> R.drawable.random_path_tile_3

            eTREE_28 -> R.drawable.tile_0028_tree
            eMUSHROOMS -> R.drawable.tile_0029_mushrooms
            eROCKY_PATCH -> R.drawable.tile_0043_rocky_patch
            eGRASS_BLANK -> R.drawable.tile_0000_blank_grass
            eGRASS_NORMAL -> R.drawable.tile_0001_normal_grass
            eGRASS_FLOWERS -> R.drawable.tile_0002_with_flowers
            eTREE_YELLOW -> R.drawable.tile_0027_yellow_tree

            eWATER_TOP_CENTER -> R.drawable.water_top_center
            eWATER_TOP_LEFT -> R.drawable.water_top_left
            eWATER_TOP_RIGHT -> R.drawable.water_top_right
            eWATER_BOTTOM_CENTER -> R.drawable.water_bottom_center
            eWATER_BOTTOM_LEFT -> R.drawable.water_bottom_left
            eWATER_CENTER -> R.drawable.water_center
            eWATER_CENTER_LEFT -> R.drawable.water_center_left
            eWATER_CENTER_RIGHT -> R.drawable.water_center_right
            eWATER_LOW_RIGHT -> R.drawable.water_low_right

            // Default fallback
            else -> R.drawable.grass_tile
        }

        // Use our custom loadSpriteSheet to produce a Bitmap and wrap it in a BitmapPainter
        BitmapPainter(loadSpriteSheet(context.resources, resourceId))
    }

    // For not within the Screen, skip rendering
    if (!objectSizeAndViewManager.IsObjectInScreen(gameObject.getCollider())) {
        return
    }

    // Use xPos and yPos for rendering
    // val xPos = remember { Animatable(gameObject.getXPos().toFloat()) }
    // val yPos = remember { Animatable(gameObject.getYPos().toFloat()) }

    // Change to use World Coord for Screen X Y Pos system for trigger rendering
    val xScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldX)
    val yScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldY)

    val density = LocalDensity.current
    val boxSize = with(density) { objectSizeAndViewManager.GET_OBJECT_SIZE().toFloat().toDp() }

    var lastColor by remember { mutableStateOf(Color.DarkGray) }
    var filterOpacity by remember { mutableStateOf(0f) }

    // snapShotMap stored the interaction timestamp in ms
    // If no ID is registered, response is 0L
    val isBeingInteracted = remember(gameObject.getID()) {
        derivedStateOf {
            colliderManager.heroInteractedToOther[gameObject.getID()] ?: 0L
        }
    }

    // Launch a side effect to process the interaction object reaction
    LaunchedEffect(isBeingInteracted.value) {
        // Only process when triggered with a valid timestamp
        if (isBeingInteracted.value > 0L) {
            // Demonstration of changing color on interaction
            lastColor = if (lastColor == Color.DarkGray) Color.Blue else Color.DarkGray

            // under different object configuration to set the object data
            // TO DO: Set Destroy, Active or InActive if needed
            if (gameObject.getCollider().isInteractable()) {
                filterOpacity = 0.8f
            }
        }
    }

    // Fade out the color filter if used
    LaunchedEffect(filterOpacity) {
        if (filterOpacity > 0f) {
            delay(50)
            filterOpacity -= 0.3f
        } else {
            filterOpacity = 0f
        }
    }

    // Debug log (if needed)
    // Log.i("Object", "Type ${gameObject.getObjType()}  id : ${gameObject.getID()} x : ${gameObject.getXPos()}  y : ${gameObject.getYPos()}")

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.TopStart)
                // don't use graphicLayer since its transformation would auto scale & translate
                // and may cause edge residue for the rendering
                .absoluteOffset {
                    IntOffset(
                        gameObject.getXPos().toInt() - xScreenPos.toInt(),
                        gameObject.getYPos().toInt() - yScreenPos.toInt()
                    )
                }
                .background(Color.Transparent)
        ) {
            // Draw the object, applying a color filter for certain interactable objects
            when (gameObject.getObjType()) {
                // Grass
                eGRASS -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Grass tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Rock (two variants)
                eROCK, eROCK_1 -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "rock1_1",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            Color.White.copy(alpha = filterOpacity),
                            BlendMode.SrcAtop
                        )
                    )
                }

                // Tree
                eTREE -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "tree tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // For all other enumerated types, we can display as-is (if no special logic needed)
                // We'll default to the same loaded bitmap, without special filter.
                else -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Default or other tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}