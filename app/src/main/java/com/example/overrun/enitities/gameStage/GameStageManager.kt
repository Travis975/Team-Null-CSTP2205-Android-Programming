package com.example.overrun.enitities.gameStage

import android.content.Context
import com.example.overrun.enitities.eGameStage
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.eObjectType.*
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.gameobject.GameObject
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.random.nextUInt

class GameStageManager(private val eStage: eGameStage) {

    val curGameStage: eGameStage = eStage

    fun InitGameStage(
        context: Context,
        gameVM: GameViewModel,
        screenWidth: UInt,
        screenHeight: UInt
    ) {
        val colliderManager = gameVM.colliderManager
        val hero = gameVM.hero
        val gameObjects = gameVM.gameObjects
        val gameObjSizeAndViewManager = gameVM.objectSizeAndViewManager

        // Important, Update the screen width and height pixel for the game
        // such that it can automatically scale down or up for different device
        gameObjSizeAndViewManager.updateScreenSize(screenWidth, screenHeight)

        val tileSize = gameObjSizeAndViewManager.GET_OBJECT_SIZE()


        gameObjects.clear()

        when (curGameStage) {
            eGameStage.eStage1 -> {

                // 1 - Load Stage Map
                val map2DInt = context.readMapFileInto2DIntArray("map1.txt")

                // 2 - Under the map design create and store the GameObject
                val mapRows = map2DInt.size
                val mapCols = map2DInt[0].size

                // 3 - Update Map Size to Manager
                gameObjSizeAndViewManager.updateCurrentMapRowColSize(mapRows.toUInt(), mapCols.toUInt())

                // 4 - Update hero starting World X Y pos
                val worldWidth = mapCols.toUInt() * tileSize
                val worldHeight = mapRows.toUInt() * tileSize
                val heroHalfWidth = (hero.getCollider().getSizeWidth() / 2U)
                val heroHalfHeight = (hero.getCollider().getSizeHeight() / 2U)
                val xStartWorldPos = (worldWidth / 2u) - heroHalfWidth
                val yStartWorldPos = (worldHeight / 2u) - heroHalfHeight

                gameObjSizeAndViewManager.InitScreenWorldXYPos(xStartWorldPos, yStartWorldPos, hero)
                hero.updatePosition(xStartWorldPos, yStartWorldPos)

                // 5 - Create Default ground object
                val stageGroundObjectType = eGRASS

                map2DInt.withIndex().forEach{ (rowIdx, row)->

                    row.withIndex().forEach{ (colIdx, objectTypeNum)->

                        gameObjects.add(
                            GameObject(
                                id = "${stageGroundObjectType.value}_${colIdx}_${rowIdx}",
                                objType = stageGroundObjectType,
                                objectSizeAndViewManager = gameObjSizeAndViewManager,
                                interactable = stageGroundObjectType.isInteractable(),
                                x = colIdx.toUInt() * tileSize,
                                y = rowIdx.toUInt() * tileSize
                            )
                        )
                    }
                }

                // Loop 2D Map to create Object, and by pass the ground object type
                map2DInt.withIndex().forEach{ (rowIdx, row)->

                    row.withIndex().forEach{ (colIdx, objectTypeNum)->

                        val objectType = eObjectType.fromValue(objectTypeNum)

                        if (objectType != stageGroundObjectType)
                        {
                            gameObjects.add(
                                GameObject(
                                    id = "${objectTypeNum}_${colIdx}_${rowIdx}",
                                    objType = objectType,
                                    objectSizeAndViewManager = gameObjSizeAndViewManager,
                                    interactable = objectType.isInteractable(),
                                    x = colIdx.toUInt() * tileSize,
                                    y = rowIdx.toUInt() * tileSize
                                )
                            )
                        }
                    }
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
