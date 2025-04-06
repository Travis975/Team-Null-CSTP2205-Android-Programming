package com.example.overrun.enitities.gameStage

import android.content.Context
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.eGameStage

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
               Stage1Configuration(context, gameVM)
            }
            eGameStage.eStage2 -> {

                Stage2Configuration(context, gameVM)
            }
            eGameStage.eStage3 -> {

                Stage3Configuration(context, gameVM)
            }
            else -> {}
        }
    }
}
