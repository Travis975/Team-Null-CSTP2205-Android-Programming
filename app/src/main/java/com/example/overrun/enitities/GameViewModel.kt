package com.example.overrun.enitities

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.gameStage.GameMetrics
import com.example.overrun.enitities.gameobject.GameObject

// Update: game time should be handled in the view model entirely for consistency
// And helps prevent memory leaks...hopefully

class GameViewModel : ViewModel(){
    // Timer state - controls whether the timer is running
    var isTimerRunning = mutableStateOf(true)

    var gameTime = mutableStateOf(0)

    // Function to toggle the timer for pause menu
    fun toggleTimer() {
        isTimerRunning.value = !isTimerRunning.value
    }

    // Timer goes up
    fun incrementGameTime() {
        gameTime.value++
    }
    // For reset on quiting
    fun resetGameTime() {
        gameTime.value = 0
        toggleTimer()
    }

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
        // Stop timer
        toggleTimer()
        gameTime.value = 0
        gameObjects.clear()
        colliderManager.cancelCollisionCheck()

    }
}