package com.example.overrun.enitities.gameStage

import android.content.Context
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.eObjectType.eGRASS
import com.example.overrun.enitities.gameobject.GameObject

fun Stage2Configuration(context: Context,
                        gameVM: GameViewModel)
{
    val colliderManager = gameVM.colliderManager
    val hero = gameVM.hero
    val gameObjects = gameVM.gameObjects
    val gameObjSizeAndViewManager = gameVM.objectSizeAndViewManager

    // 1 - Load Stage Map
    val map2DInt = context.readMapFileInto2DIntArray("map2.txt")

    // 2 - Under the map design create and store the GameObject
    val mapRows = map2DInt.size
    val mapCols = map2DInt[0].size

    val tileSize = gameObjSizeAndViewManager.GET_OBJECT_SIZE()

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
    val stageGroundObjectType = eObjectType.eGRASS_NORMAL

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

            val objectType = eObjectType.fromValue(objectTypeNum)!!

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

    // 6) Setup colliders
    colliderManager.resetAllCollider()
    colliderManager.setHeroCollider(hero)

    // Skip grass for collisions so hero can move over it
    colliderManager.setObjectColliders(
        gameObjects
            .filter { !it.getObjType().isStatic()}
            .toMutableList() // convert to MutableList
    )

    // Start Coroutine Check Action Collider
    colliderManager.startHeroActionCollisionCheck()
}
