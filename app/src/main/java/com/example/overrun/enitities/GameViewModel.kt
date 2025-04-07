package com.example.overrun.enitities

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.overrun.enitities.character.EnemyCharacter
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.gameStage.GameEnemyFactory
import com.example.overrun.enitities.gameStage.GameMetricsAndControl
import com.example.overrun.enitities.gameobject.GameObject

class GameViewModel : ViewModel(){

    // Trigger Stage Start
    var isStageStartRender = mutableStateOf(true)
    fun triggerStageStartRender() {
        isStageStartRender.value = !isStageStartRender.value
        SetTimerRunStop(true)
    }

    // Timer state
    var isTimerRunning = mutableStateOf(true) // This controls whether the timer is running

    // Function to toggle the timer for pause menu
    fun SetTimerRunStop(isRun : Boolean) {
        isTimerRunning.value = isRun
    }

    // Track the current map for leaderboards, set default to level 1
    var currentMap = mutableStateOf("Spooky Forest")

    fun setCurrentMap(mapName: String) {
        currentMap.value = mapName
    }

    fun getCurrentMap(): String {
        return currentMap.value
    }

    val gameMetricsAndCtrl = GameMetricsAndControl()

    val colliderManager = ColliderManager()

    val objectSizeAndViewManager = GameObjectSizeAndViewManager()

    var gameEnemyFactory : GameEnemyFactory? = null

    // Hero data object
    // Important, use viewModel to own the character for persisting the character state across the composable screen
    // and having the game life-cycle
    val hero = HeroCharacter(objectSizeAndViewManager)

    // would be manipulate through the EnemyFactory for creation and destruction
    val enemies = mutableStateListOf<EnemyCharacter>()

    fun stopAllEnemiesMoveThread()
    {
        enemies.forEach { enemy ->
            enemy.runningMoveThread = false
        }
    }
    fun destructEnemyFactoryRoutine()
    {
        stopAllEnemiesMoveThread()

        gameEnemyFactory?.cancelSpawnEnemy()
    }

    //val enemies : MutableList<EnemyCharacter> = arrayListOf()
    val gameObjects = mutableStateListOf<GameObject>()

    fun setGameObjectDestroyByID(id : String) : Boolean
    {
        val gameObject = gameObjects.filter{it.getID() == id}.firstOrNull()
        val isAlreadyDestoryed = if (gameObject != null) gameObject.getIsDestroy() else false
        if (!isAlreadyDestoryed)
        {
            gameObject?.setDestroy()
            return true
        }
        return false
    }

    fun dynamicGameObjectCreation(eObjectType: eObjectType,
                                    x : UInt, y : UInt)
    {
        val gameObj = GameObject(
                            id = "${eObjectType.value}_${x}_${y}",
                            objType = eObjectType,
                            objectSizeAndViewManager = objectSizeAndViewManager,
                            interactable = eObjectType.isInteractable(),
                            blockable = eObjectType.isColliderBlockable(),
                            x = x,
                            y = y
                        )
        gameObjects.add(gameObj)

        if (!eObjectType.isStatic())
        {
            colliderManager.addObjectCollider(gameObj)
        }
    }

    // when gameViewModel destruct
    override fun onCleared() {
        gameObjects.clear()
        colliderManager.cancelCollisionCheck()
        destructEnemyFactoryRoutine()
    }
}