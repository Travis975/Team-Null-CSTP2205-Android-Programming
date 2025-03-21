package com.example.overrun.enitities

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.character.EnemyCharacter
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.gameStage.GameMetrics
import com.example.overrun.enitities.gameobject.GameObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel(){

    // Timer state
    var isTimerRunning = mutableStateOf(true) // This controls whether the timer is running

    // Function to toggle the timer for pause menu
    fun toggleTimer() {
        isTimerRunning.value = !isTimerRunning.value
    }

    val gameMetrics = GameMetrics()

    val colliderManager = ColliderManager(this)

    val objectSizeAndViewManager = GameObjectSizeAndViewManager()

    // Hero data object
    // Important, use viewModel to own the character for persisting the character state across the composable screen
    // and having the game life-cycle
    val hero = HeroCharacter(objectSizeAndViewManager)

    // Would be created through ObjectFactory after through the Game Manager Stage Init
    //val enemies : MutableList<EnemyCharacter> = arrayListOf()
    public val _gameObjects = java.util.concurrent.CopyOnWriteArrayList<GameObject>()
    val gameObjects: List<GameObject> get() = _gameObjects

    public fun addGameObject(obj: GameObject) {
        _gameObjects.add(obj)
        // Add to appropriate collider list
        if (obj is EnemyCharacter) {
            colliderManager.addEnemyCollider(obj)
        } else {
            colliderManager.addObjectCollider(obj)
        }
    }

    public fun removeGameObject(obj: GameObject) {
        _gameObjects.remove(obj)
    }

    // when gameViewModel destruct
    override fun onCleared() {
        stopEnemyUpdates()
        _gameObjects.clear()
        colliderManager.cancelCollisionCheck()

    }

    // Enemy couroutine 
    private var _enemyUpdateJob: Job? = null
    private var _enemyUpdateScope = CoroutineScope(Dispatchers.Default)

    fun updateEnemies() {
        _gameObjects.forEach { gameObject ->
            if (gameObject is EnemyCharacter) {
                gameObject.updatePositionTowardsHero() // Continuously called
            }
        }
    }

    fun startEnemyUpdates() {
        _enemyUpdateJob?.cancel()
        _enemyUpdateJob = _enemyUpdateScope.launch {
            while (true) {
                updateEnemies() // Updates all enemies every frame
                delay(16L) // ~60 FPS
            }
        }
    }
    fun stopEnemyUpdates() {
        _enemyUpdateJob?.cancel()
    }



}