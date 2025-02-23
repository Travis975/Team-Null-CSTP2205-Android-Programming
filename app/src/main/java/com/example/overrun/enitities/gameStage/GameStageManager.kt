package com.example.overrun.enitities.gameStage

import com.example.gohero.enitities.GameConstant.DEFAULT_OBJECT_SIZE
import com.example.gohero.enitities.eGameStage
import com.example.gohero.enitities.eObjectType
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.gameobject.GameObject

class GameStageManager(eStage: eGameStage) {

    // Stages configuration
    val curGameStage : eGameStage = eStage

    // Init and Setup Game Stage
    public fun InitGameStage(gameVM: GameViewModel,
                             screenWidth : UInt, screenHeight : UInt)
    {
        // 0 - Init the game View Model Objects
        val colliderManager = gameVM.colliderManager
        val hero = gameVM.hero
        val gameObjects = gameVM.gameObjects
        //val enemies = gameVM.enemies

        // update hero starting position
        val xStartPos = (screenWidth / 2u) - hero.getCollider().getSizeWidth()
        val yStartPos = (screenHeight / 2u) - hero.getCollider().getSizeHeight()
        hero.updatePosition(xStartPos, yStartPos)
        gameObjects.clear()

        // 1 - Create Game Stage Object related to the Stage
        // Load the Map to create object
        // Some rules applied to the stage etc.
        when (curGameStage)
        {
            eGameStage.eStage1->{

                // Temporary hard code creation for testing
                // Should be created through ObjectFactory after through the game level map
                // Position in Pixel
                gameVM.gameObjects.add(
                    GameObject(eObjectType.eROCK.toString() + "1", eObjectType.eROCK,
                                    width = DEFAULT_OBJECT_SIZE, height = DEFAULT_OBJECT_SIZE,
                                    300U, 300U)
                )

            }
            eGameStage.eStage2->{

            }
            eGameStage.eStage3-> {

            }
            else->{}
        }

        // 2 -  Setup Collider
        colliderManager.resetAllCollider()
        colliderManager.setHeroCollider(gameVM.hero)
        colliderManager.setObjectColliders(gameVM.gameObjects)
    }
}