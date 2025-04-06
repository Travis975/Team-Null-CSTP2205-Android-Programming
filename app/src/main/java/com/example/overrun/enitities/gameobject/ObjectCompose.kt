package com.example.overrun.enitities.gameobject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.example.overrun.R
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.character.CharacterBase
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.collider.ColliderManager.eColliderType
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.eObjectType.*
import com.example.overrun.enitities.gameStage.GameMetricsAndControl
import com.example.overrun.enitities.sprites.loadSpriteSheet
import kotlinx.coroutines.delay

@Composable
fun ObjectCompose(
    gameObject: GameObject,
    gameMetricsAndCtrl: GameMetricsAndControl,
    colliderManager: ColliderManager,
    objectSizeAndViewManager : GameObjectSizeAndViewManager,
    hero : CharacterBase
) {
    // If this object was flagged for destruction, skip drawing
    if (gameObject.getIsDestroy()) {
        return
    }
    val context = LocalContext.current

    // Create a remembered BitmapPainter to store/cache the tile image
    // so we don't recreate it on every render.
    val objectbitmapPainter = remember(gameObject.getObjType()) {
        // Map each eObjectType to its corresponding drawable resource
        val resourceId = when (gameObject.getObjType()) {

            // Existing mappings
            eGRASS -> R.drawable.grass_tile
            eTREE, eTREE_BACKGROUND -> R.drawable.tree_1
            eROCK, eROCK_1 -> R.drawable.rock1_1
            eROCK_TOXIC -> R.drawable.rock3_toxic
            eROCK_2 -> R.drawable.rock2_1
            eSAND -> R.drawable.sand_1
            eCACTUS -> R.drawable.cactus_1

            // Additional object types
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

            // ----------------------------------------------------------------
            // Newly added assets:
            eTOXIC_ROCK_SNOW -> R.drawable.toxic_rock_snow
            eTOXIC_SHRUB -> R.drawable.toxic_shrub
            eTOXIC_TREE_TOP -> R.drawable.toxic_tree_top
            eTOXIC_TREE_BOTTOM -> R.drawable.toxic_tree_bottom

            eHALF_TREE_OBSTACLE -> R.drawable.half_tree_obstacle
            eSNOWMAN -> R.drawable.snowman
            eWATER_IN_WHITE -> R.drawable.water_in_white
            eBLUE_ARROW -> R.drawable.blue_arrow
            eRED_FLAG -> R.drawable.red_flag
            eBLUE_FLAG -> R.drawable.blue_flag
            eRED_ARROW -> R.drawable.red_arrow
            eSNOW_BUSH -> R.drawable.snow_bush
            eSNOW_TREE_TOP -> R.drawable.snow_tree_top
            eSNOW_TREE_BOTTOM -> R.drawable.snow_tree_bottom

            eWHITE_SNOW_BLANK -> R.drawable.white_snow_blank
            eWHITE_SNOW_PATCHES_1 -> R.drawable.white_snow_patches_1
            eWHITE_SNOW_PATCHES_2 -> R.drawable.white_snow_patches_2
            eWHITE_SNOW_PATCHES_3 -> R.drawable.white_snow_patches_3
            eWHITE_SNOW_PATCHES_4 -> R.drawable.white_snow_patches_4

            eGREY_SNOW_BLANK -> R.drawable.grey_snow_blank
            eGREY_SNOW_PATCHES_1 -> R.drawable.grey_snow_patches_1
            eGREY_SNOW_PATCHES_2 -> R.drawable.grey_snow_patches_2
            eGREY_SNOW_PATCHES_3 -> R.drawable.grey_snow_patches_3
            eGREY_SNOW_PATCHES_4 -> R.drawable.grey_snow_patches_4

            // Fallback if unrecognized
            else -> R.drawable.grass_tile
        }

        // Use our custom loadSpriteSheet to produce a Bitmap and wrap it in a BitmapPainter
        BitmapPainter(loadSpriteSheet(context.resources, resourceId))
    }

    // Skip drawing if not within screen
    if (!objectSizeAndViewManager.IsObjectInScreen(gameObject.getCollider())) {
        return
    }

    // Retrieve the current screen offsets
    val xScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldX)
    val yScreenPos by rememberUpdatedState(objectSizeAndViewManager.screenWorldY)

    val density = LocalDensity.current
    val boxSize = with(density) { objectSizeAndViewManager.GET_OBJECT_SIZE().toFloat().toDp() }

    var lastColor by remember { mutableStateOf(Color.DarkGray) }
    var filterOpacity by remember { mutableStateOf(0f) }

    // Retrieve timestamp from hero-object interaction map
    val isBeingInteracted = remember(gameObject.getID()) {
        derivedStateOf {
            colliderManager.heroInteractedToOther[eColliderType.eCollideObject]!![gameObject.getID()] ?: 0L
        }
    }

    // Process changes in the interaction state
    LaunchedEffect(isBeingInteracted.value) {
        // Only process when timestamp > 0
        if (isBeingInteracted.value > 0L) {

            if (gameObject.getObjType() == eObjectType.eMUSHROOMS) {
                // Example: mushrooms add +1 life and then destroy themselves
                hero.setLives(hero.getLives() + 1U)
                gameObject.setDestroy()
                return@LaunchedEffect
            }

            // Demonstration of changing color on interaction
            lastColor = if (lastColor == Color.DarkGray) Color.Blue else Color.DarkGray

            // If collider is marked as interactable, briefly flash the tint
            if (gameObject.getCollider().isInteractable()) {
                filterOpacity = 0.8f
                gameMetricsAndCtrl.addHeroHitCount()
            }
        }
    }

    // Fades out the tint effect
    LaunchedEffect(filterOpacity) {
        if (filterOpacity > 0f) {
            delay(50)
            filterOpacity -= 0.3f
        } else {
            filterOpacity = 0f
        }
    }

    // Render the object
    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.TopStart)
                .absoluteOffset {
                    IntOffset(
                        gameObject.getXPos().toInt() - xScreenPos.toInt(),
                        gameObject.getYPos().toInt() - yScreenPos.toInt()
                    )
                }
                .background(Color.Transparent)
        ) {
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

                // Sand
                eSAND -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Sand tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Cactus
                eCACTUS -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Cactus tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Rocks (and other 'toxic' or tinted obstacles)
                eROCK, eROCK_1, eROCK_TOXIC, eROCK_2,
                eTOXIC_ROCK_SNOW, eTOXIC_SHRUB, eTOXIC_TREE_TOP, eTOXIC_TREE_BOTTOM -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Rock or Toxic tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            Color.White.copy(alpha = filterOpacity),
                            BlendMode.SrcAtop
                        )
                    )
                }

                // Regular tree or tree background
                eTREE, eTREE_BACKGROUND -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Tree tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Water tiles
                eWATER_TOP_CENTER, eWATER_TOP_LEFT, eWATER_TOP_RIGHT,
                eWATER_BOTTOM_CENTER, eWATER_BOTTOM_LEFT, eWATER_CENTER,
                eWATER_CENTER_LEFT, eWATER_CENTER_RIGHT, eWATER_LOW_RIGHT,
                eWATER_IN_WHITE -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Water tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Misc new items that render normally (no tint logic)
                eHALF_TREE_OBSTACLE, eSNOWMAN, eBLUE_ARROW, eRED_FLAG,
                eBLUE_FLAG, eRED_ARROW, eSNOW_BUSH, eSNOW_TREE_TOP,
                eSNOW_TREE_BOTTOM -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Obstacle or decoration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Snow tile sets
                eWHITE_SNOW_BLANK, eWHITE_SNOW_PATCHES_1, eWHITE_SNOW_PATCHES_2,
                eWHITE_SNOW_PATCHES_3, eWHITE_SNOW_PATCHES_4,
                eGREY_SNOW_BLANK, eGREY_SNOW_PATCHES_1, eGREY_SNOW_PATCHES_2,
                eGREY_SNOW_PATCHES_3, eGREY_SNOW_PATCHES_4 -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Snow tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Additional plants/grass
                eTREE_28, eMUSHROOMS, eGRASS_BLANK, eGRASS_NORMAL,
                eGRASS_FLOWERS, eTREE_YELLOW, eROCKY_PATCH -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Plants tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Path tiles
                ePATH_RANDOM_3, ePATH_BLANK_MUD, ePATH_LEFT_BOUNDARY,
                ePATH_RIGHT_BOUNDARY, ePATH_RANDOM, ePATH_RANDOM_2 -> {
                    Image(
                        painter = objectbitmapPainter,
                        contentDescription = "Path tile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // All else (fallback)
                else -> {
                    // Default magenta box for unknown object types
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
