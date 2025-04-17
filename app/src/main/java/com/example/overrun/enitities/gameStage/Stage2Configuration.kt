package com.example.overrun.enitities.gameStage

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.overrun.enitities.GameConstant
import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_SPEED
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.eEnemyType
import com.example.overrun.enitities.eHeroType
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.eObjectType.*
import com.example.overrun.enitities.gameobject.GameObject

fun Stage2Configuration(context: Context,
                        gameVM: GameViewModel)
{
    val colliderManager = gameVM.colliderManager
    val hero = gameVM.hero
    val gameMetricsAndCtrl = gameVM.gameMetricsAndCtrl
    val gameObjects = gameVM.gameObjects
    val gameObjSizeAndViewManager = gameVM.objectSizeAndViewManager

    // 0 - Clear Metrics and Reset
    gameMetricsAndCtrl.resetCounter()
    gameVM.gameMetricsAndCtrl.isGamePaused.value = false
    gameVM.SetTimerRunStop(true) // Resume the timer
    hero.reset(GameConstant.DEFAULT_LIVES)

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

    when (hero.getHeroType())
    {
        eHeroType.eHERO_TOKAGE_ORANGE -> hero.setSpeed((DEFAULT_HERO_SPEED.toDouble() * 1.5).toUInt())
        eHeroType.eHERO_TOKAGE_YELLOW -> hero.setSpeed(DEFAULT_HERO_SPEED * 2U)
        else->hero.setSpeed(DEFAULT_HERO_SPEED)
    }
    hero.updatePosition(xStartWorldPos, yStartWorldPos)

    // 5 - Create Default ground object
    val stageGroundObjectType = eSAND

    map2DInt.withIndex().forEach{ (rowIdx, row)->

        row.withIndex().forEach{ (colIdx, objectTypeNum)->

            gameObjects.add(
                GameObject(
                    id = "${stageGroundObjectType.value}_${colIdx}_${rowIdx}",
                    objType = stageGroundObjectType,
                    objectSizeAndViewManager = gameObjSizeAndViewManager,
                    interactable = stageGroundObjectType.isInteractable(),
                    blockable = stageGroundObjectType.isColliderBlockable(),
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
                        blockable = objectType.isColliderBlockable(),
                        x = colIdx.toUInt() * tileSize,
                        y = rowIdx.toUInt() * tileSize
                    )
                )
            }
        }
    }

    // 6) Setup EnemyFactory
    gameVM.stopAllEnemiesMoveThread()
    gameVM.enemies.clear()

    // Design Stage 2 exists enemy and configuration
    gameVM.currentEnemyList = listOf(

        // Parrot enemy
        EnemyConfiguration(
            eType = eEnemyType.eENEMY_PARROT,
            speed = (DEFAULT_ENEMY_SPEED.toFloat() * 2.0f).toUInt()     // parrot is faster
        ),

        // Slime enemy
        EnemyConfiguration(
            eType = eEnemyType.eENEMY_SLIME
        )
    )
    gameVM.gameMetricsAndCtrl.setStageEnemySize(5U)    // 30 Enemies at most

    val healthDropRate = if (gameVM.is100PercentHealthDrop.value) 1f else 0.2f // 20 % to have gem drop off

    gameVM.gameEnemyFactory = GameEnemyFactory(gameVM.enemies,
                                                gameVM.currentEnemyList,
                                                gameVM.gameMetricsAndCtrl.getEnemySize(), // Max have at most 30 enemies
                                                healthDropRate,
                                                listOf(1L, 2L, 4L, 8L),  // list of intervals in second to be random pick
                                                gameMetricsAndCtrl,
                                                colliderManager,
                                                gameObjSizeAndViewManager)

    gameVM.gameEnemyFactory?.resetEnemyUniqueID()
    gameVM.gameEnemyFactory?.startCheckAndSpawnEnemy()


    // 7) Setup colliders
    colliderManager.resetAllCollider()
    colliderManager.setHeroCollider(hero)

    // Skip grass for collisions so hero can move over it
    colliderManager.setObjectColliders(
        gameObjects
            .filter { !it.getObjType().isStatic()}
            .toMutableList() // convert to MutableList
    )

    // Start Coroutine Check Action Collider
    colliderManager.startCollisionCheck()
}

