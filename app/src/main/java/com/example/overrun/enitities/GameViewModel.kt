package com.example.overrun.enitities

import androidx.lifecycle.ViewModel
import com.example.gohero.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.gameobject.GameObject

class GameViewModel : ViewModel(){

    val colliderManager = ColliderManager()

    // Hero data object
    // Important, use viewModel to own the character for persisting the character state across the composable screen
    // and having the game life-cycle
    val hero = HeroCharacter()

    // Would be created through ObjectFactory after through the Game Manager Stage Init
    //val enemies : MutableList<EnemyCharacter> = arrayListOf()
    val gameObjects : MutableList<GameObject> = arrayListOf()
}