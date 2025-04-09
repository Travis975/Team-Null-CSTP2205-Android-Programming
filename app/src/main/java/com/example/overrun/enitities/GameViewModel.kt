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
import com.example.overrun.repository.FirestoreRepository

class GameViewModel : ViewModel(){


    private val firestoreRepository = FirestoreRepository()

    // Maps to track completed challenges
    val levelChallengesCompleted = mutableMapOf("level2" to false, "level3" to false)
    val characterChallengesCompleted = mutableMapOf("character2" to false, "character3" to false)

    // Function to complete a challenge and save it to Firestore
    fun completeChallenge(level: String? = null, character: String? = null, userId: String) {
        level?.let {
            levelChallengesCompleted[it] = true
        }
        character?.let {
            characterChallengesCompleted[it] = true
        }

        // Save updated challenge completion data to Firestore
        saveChallengesToFirestore(userId)
    }

    // Function to save the challenges to Firestore
    private fun saveChallengesToFirestore(userId: String) {
        firestoreRepository.saveChallengeCompletion(userId, levelChallengesCompleted, characterChallengesCompleted)
    }

    // Function to load challenge data from Firestore when the game starts or user logs in
    fun loadChallengesFromFirestore(userId: String) {
        firestoreRepository.loadChallenges(userId) { levelChallenges, characterChallenges ->
            levelChallengesCompleted.putAll(levelChallenges)
            characterChallengesCompleted.putAll(characterChallenges)
        }
    }

    // Function to check and trigger challenge completion based on game metrics
    fun checkForChallengeCompletion(gameMetrics: GameMetricsAndControl, userId: String) {
        // Character 2 Challenge: Survive for 15 seconds
        if (gameMetrics.getTimeSurvived()
                .toInt() >= 15 && !characterChallengesCompleted["character2"]!!
        ) {
            completeChallenge(character = "character2", userId = userId)
        }

        // Character 3 Challenge: Survive for 30 seconds
        if (gameMetrics.getTimeSurvived()
                .toInt() >= 30 && !characterChallengesCompleted["character3"]!!
        ) {
            completeChallenge(character = "character3", userId = userId)
        }

        // Level 2 Challenge: Get 5 eliminations
        if (gameMetrics.getEnemyKillCount().toInt() >= 5 && !levelChallengesCompleted["level2"]!!) {
            completeChallenge(level = "level2", userId = userId)
        }

        // Level 3 Challenge: Get 10 eliminations
        if (gameMetrics.getEnemyKillCount()
                .toInt() >= 10 && !levelChallengesCompleted["level3"]!!
        ) {
            completeChallenge(level = "level3", userId = userId)
        }
    }

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
    val gameObjects : MutableList<GameObject> = arrayListOf()

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

    // when gameViewModel destruct
    override fun onCleared() {
        gameObjects.clear()
        colliderManager.cancelCollisionCheck()
        destructEnemyFactoryRoutine()
    }
}