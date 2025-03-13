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

        gameObjects.clear()

        when (curGameStage) {
            eGameStage.eStage1 -> {
               Stage1Configuration(context,
                                    hero, gameObjects,
                                    colliderManager, gameObjSizeAndViewManager)
            }
            eGameStage.eStage2 -> {


            }
            // Other stages...
            else -> {}
        }
    }
}
