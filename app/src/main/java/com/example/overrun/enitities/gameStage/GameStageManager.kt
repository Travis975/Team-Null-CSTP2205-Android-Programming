package com.example.overrun.enitities.gameStage

import com.example.gohero.enitities.GameConstant.DEFAULT_OBJECT_SIZE
import com.example.gohero.enitities.eGameStage
import com.example.gohero.enitities.eObjectType
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.gameobject.GameObject
import kotlin.math.ceil

class GameStageManager(private val eStage: eGameStage) {

    val curGameStage: eGameStage = eStage

    fun InitGameStage(
        gameVM: GameViewModel,
        screenWidth: UInt,
        screenHeight: UInt
    ) {
        val colliderManager = gameVM.colliderManager
        val hero = gameVM.hero
        val gameObjects = gameVM.gameObjects

        // Convert UInt -> Int for arithmetic
        val screenWidthInt = screenWidth.toInt()
        val screenHeightInt = screenHeight.toInt()
        val tileSizeInt = DEFAULT_OBJECT_SIZE.toInt()

        // Use ceil(...) so we don’t leave gaps at the edges for grass
        val numTilesX = ceil(screenWidthInt.toDouble() / tileSizeInt.toDouble()).toInt()
        val numTilesY = ceil(screenHeightInt.toDouble() / tileSizeInt.toDouble()).toInt()

        // Center hero on screen
        val xStartPos = (screenWidth / 2u) - hero.getCollider().getSizeWidth()
        val yStartPos = (screenHeight / 2u) - hero.getCollider().getSizeHeight()
        hero.updatePosition(xStartPos, yStartPos)

        gameObjects.clear()

        when (curGameStage) {
            eGameStage.eStage1 -> {
                // --------------------------------------------------
                // 1) Fill background with grass
                // --------------------------------------------------
                for (tileY in 0 until numTilesY) {
                    for (tileX in 0 until numTilesX) {
                        val posX = tileX * tileSizeInt
                        val posY = tileY * tileSizeInt

                        gameObjects.add(
                            GameObject(
                                id = "Grass_${tileX}_${tileY}",
                                objType = eObjectType.eGRASS,
                                width = DEFAULT_OBJECT_SIZE,
                                height = DEFAULT_OBJECT_SIZE,
                                x = posX.toUInt(),
                                y = posY.toUInt()
                            )
                        )
                    }
                }

                // --------------------------------------------------
                // 2) Add a ring of rocks exactly on screen edges
                //    (left, right, top, bottom) for a symmetrical wall
                // --------------------------------------------------

                // We define the absolute edges
                val tileSize = tileSizeInt
                val leftX = 0
                val rightX = (screenWidthInt - tileSize).coerceAtLeast(0)
                val topY = 0
                val bottomY = (screenHeightInt - tileSize).coerceAtLeast(0)

                // Top & bottom rows: loop across the full screen width
                for (curX in 0..screenWidthInt step tileSize) {
                    // Top row
                    gameObjects.add(
                        GameObject(
                            id = "Rock_top_$curX",
                            objType = eObjectType.eTREE,
                            width = DEFAULT_OBJECT_SIZE,
                            height = DEFAULT_OBJECT_SIZE,
                            x = curX.toUInt(),
                            y = topY.toUInt()
                        )
                    )
                    // Bottom row
                    gameObjects.add(
                        GameObject(
                            id = "Rock_bottom_$curX",
                            objType = eObjectType.eTREE,
                            width = DEFAULT_OBJECT_SIZE,
                            height = DEFAULT_OBJECT_SIZE,
                            x = curX.toUInt(),
                            y = bottomY.toUInt()
                        )
                    )
                }

                // Left & right columns: loop across the full screen height
                for (curY in 0..screenHeightInt step tileSize) {
                    // Left column
                    gameObjects.add(
                        GameObject(
                            id = "Rock_left_$curY",
                            objType = eObjectType.eTREE,
                            width = DEFAULT_OBJECT_SIZE,
                            height = DEFAULT_OBJECT_SIZE,
                            x = leftX.toUInt(),
                            y = curY.toUInt()
                        )
                    )
                    // Right column
                    gameObjects.add(
                        GameObject(
                            id = "Rock_right_$curY",
                            objType = eObjectType.eTREE,
                            width = DEFAULT_OBJECT_SIZE,
                            height = DEFAULT_OBJECT_SIZE,
                            x = rightX.toUInt(),
                            y = curY.toUInt()
                        )
                    )
                }
            }

            // Other stages...
            else -> {}
        }

        // --------------------------------------------------
        // 3) Setup colliders
        // --------------------------------------------------
        colliderManager.resetAllCollider()
        colliderManager.setHeroCollider(hero)

        // Skip grass for collisions so hero can move over it
        colliderManager.setObjectColliders(
            gameObjects
                .filter { it.getObjType() != eObjectType.eGRASS }
                .toMutableList() // convert to MutableList
        )
    }
}
