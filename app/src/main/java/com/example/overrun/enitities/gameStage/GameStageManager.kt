package com.example.overrun.enitities.gameStage

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
        val gameObjSizeManager = gameVM.objectSizeManager

        // Important, Update the screen width and height pixel for the game
        // such that it can automatically scale down or up for different device
        gameObjSizeManager.updateScreenSize(screenWidth, screenHeight)

        val tileSize = gameObjSizeManager.GET_OBJECT_SIZE()

        // Use ceil(...) so we don’t leave gaps at the edges for grass
        val numTilesX = ceil(screenWidth.toFloat() / tileSize.toFloat()).toUInt()
        val numTilesY = ceil(screenHeight.toFloat() / tileSize.toFloat()).toUInt()

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
                for (tileY in 0U until numTilesY) {
                    for (tileX in 0U until numTilesX) {
                        val posX = tileX * tileSize
                        val posY = tileY * tileSize

                        gameObjects.add(
                            GameObject(
                                id = "Grass_${tileX}_${tileY}",
                                objType = eObjectType.eGRASS,
                                objectSizeManager = gameObjSizeManager,
                                interactable = false,
                                x = posX,
                                y = posY
                            )
                        )
                    }
                }

                // --------------------------------------------------
                // 2) Add a ring of rocks exactly on screen edges
                //    (left, right, top, bottom) for a symmetrical wall
                // --------------------------------------------------

                // We define the absolute edges
                val rightX = (screenWidth - tileSize).coerceAtLeast(0U)
                val bottomY = (screenHeight - tileSize).coerceAtLeast(0U)

                // Top & bottom rows: loop across the full screen width
                for (curX in 0U..screenWidth step tileSize.toInt()) {
                    // Top row
                    gameObjects.add(
                        GameObject(
                            id = "Rock_top_$curX",
                            objType = eObjectType.eTREE,
                            objectSizeManager = gameObjSizeManager,
                            interactable = false,
                            x = curX,
                            y = 0U
                        )
                    )
                    // Bottom row
                    gameObjects.add(
                        GameObject(
                            id = "Rock_bottom_$curX",
                            objType = eObjectType.eTREE,
                            objectSizeManager = gameObjSizeManager,
                            interactable = false,
                            x = curX,
                            y = bottomY
                        )
                    )
                }

                // Left & right columns: loop across the full screen height
                for (curY in 0U..screenHeight step tileSize.toInt()) {
                    // Left column
                    gameObjects.add(
                        GameObject(
                            id = "Rock_left_$curY",
                            objType = eObjectType.eTREE,
                            objectSizeManager = gameObjSizeManager,
                            interactable = false,
                            x = 0U,
                            y = curY
                        )
                    )
                    // Right column
                    gameObjects.add(
                        GameObject(
                            id = "Rock_right_$curY",
                            objType = eObjectType.eTREE,
                            objectSizeManager = gameObjSizeManager,
                            interactable = false,
                            x = rightX,
                            y = curY
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

        // Start Coroutine Check Action Collider
        colliderManager.startHeroActionCollisionCheck()
    }
}
