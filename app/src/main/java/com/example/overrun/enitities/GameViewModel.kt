package com.example.overrun.enitities

import androidx.lifecycle.ViewModel
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.gameStage.GameMetrics
import com.example.overrun.enitities.gameobject.GameObject

class GameViewModel : ViewModel(){

    val gameMetrics = GameMetrics()

    val colliderManager = ColliderManager()

    val objectSizeAndViewManager = GameObjectSizeAndViewManager()

    // Hero data object
    // Important, use viewModel to own the character for persisting the character state across the composable screen
    // and having the game life-cycle
    val hero = HeroCharacter(objectSizeAndViewManager)

    // Would be created through ObjectFactory after through the Game Manager Stage Init
    //val enemies : MutableList<EnemyCharacter> = arrayListOf()
    val gameObjects : MutableList<GameObject> = arrayListOf()

    // when gameViewModel destruct
    override fun onCleared() {
        gameObjects.clear()
        colliderManager.cancelCollisionCheck()
    }
}