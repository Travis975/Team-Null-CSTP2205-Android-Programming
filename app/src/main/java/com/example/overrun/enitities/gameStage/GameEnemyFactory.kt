package com.example.overrun.enitities.gameStage

import com.example.overrun.enitities.GameConstant.ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.character.EnemyCharacter
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.eDirection.*
import com.example.overrun.enitities.eEnemyType
import com.example.overrun.enitities.eObjectType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

// pass in the enemies list reference to manipulate
class GameEnemyFactory(private val enemies : MutableList<EnemyCharacter>,
                       private val eEnemyType : eEnemyType,
                        private val maxNumOfEnemy : UInt,
                       private val spawnIntervalSecList : List<Long>,
                       private val colliderManager : ColliderManager,
                       private val objectSizeManager : GameObjectSizeAndViewManager)
{

    private var _enemySpawnJob : Job? = null
    private var _runningChkAndSpawnEnemy = false
    private val _coroutineScope = CoroutineScope(Dispatchers.Default)

    val screenWidth = objectSizeManager.getScreenWidth().toFloat()
    val screenHeight = objectSizeManager.getScreenHeight().toFloat()

    var lastSpawnEnemyTimeSec : Long = 0L
    var nextSpawnEnemyInterval = spawnIntervalSecList[Random.nextInt(0, spawnIntervalSecList.size)]

    private var _enemyUniqueID : AtomicLong = AtomicLong(0L)

    fun resetEnemyUniqueID(){
        _enemyUniqueID = AtomicLong(0L)
    }

    fun checkAndRemoveDeadEnemy()
    {
        // Temp not to remove, had datarace problem
        // Remove collider first
//        var removeIDList : MutableList<String> = mutableListOf()
//        enemies.forEach{ enemy ->
//            if (enemy.isDieFinished())
//            {
//                removeIDList.add(enemy.getID())
//                colliderManager.removeEnemyCollider(enemy.getID())
//            }
//        }
//
//        // then remove from the pool
//        enemies.removeIf{it.getID() in removeIDList}
    }

    fun SpawnEnemy()
    {
        // Check if any existing enemy die, remove from the pool
        checkAndRemoveDeadEnemy()

        if (enemies.count() >= maxNumOfEnemy.toInt())
        {
            return
        }

        val spawnOffsetBuffer = 50f

        val currentScreenX = objectSizeManager.screenWorldX
        val currentScreenY = objectSizeManager.screenWorldY

        // eDOWN(0), eUP(1), eLEFT(2), eRIGHT(3)
        val randomEdge = Random.nextInt(0, 4) // 4 exclusive
        var randomX : UInt = 0U
        var randomY : UInt = 0U

        when (randomEdge){
            eDOWN.value->{
                randomY = currentScreenY.toUInt() + screenHeight.toUInt() + spawnOffsetBuffer.toUInt()
                randomX = Random.nextInt((currentScreenX - spawnOffsetBuffer).toInt(),
                                            (currentScreenX + screenWidth + spawnOffsetBuffer).toInt()).toUInt()
            }
            eUP.value->{
                randomY = currentScreenY.toUInt() - spawnOffsetBuffer.toUInt()
                randomX = Random.nextInt((currentScreenX - spawnOffsetBuffer).toInt(),
                    (currentScreenX + screenWidth + spawnOffsetBuffer).toInt()).toUInt()
            }
            eLEFT.value->{
                randomX = currentScreenX.toUInt() - spawnOffsetBuffer.toUInt()
                randomY = Random.nextInt((currentScreenY - spawnOffsetBuffer).toInt(),
                    (currentScreenY + screenHeight + spawnOffsetBuffer).toInt()).toUInt()
            }
            eRIGHT.value->{
                randomX = currentScreenX.toUInt() + screenWidth.toUInt() + spawnOffsetBuffer.toUInt()
                randomY = Random.nextInt((currentScreenY - spawnOffsetBuffer).toInt(),
                    (currentScreenY + screenHeight + spawnOffsetBuffer).toInt()).toUInt()
            }
        }

        val id = _enemyUniqueID.incrementAndGet()
        // id = "Enemy_drawableUnqieId_UniqueID"
        val enemy = EnemyCharacter("${eObjectType.eENEMY.value}_${eEnemyType.resId}_${id}",
                                    eEnemyType,
                                    randomX, randomY,
                                    objectSizeManager)

        enemies.add(enemy)
        colliderManager.addEnemyColliders(enemy)
    }

    public fun startCheckAndSpawnEnemy()
    {
        // Cancel if had previous coroutine
        cancelSpawnEnemy()
        _runningChkAndSpawnEnemy = true

        // Then Launch a new coroutine
        _enemySpawnJob = _coroutineScope.launch{
            while(_runningChkAndSpawnEnemy)
            {
                val start = System.currentTimeMillis()

                if (lastSpawnEnemyTimeSec == 0L)
                {
                    lastSpawnEnemyTimeSec = (System.currentTimeMillis() / 1000)
                }
                else if (((start / 1000) - lastSpawnEnemyTimeSec) > nextSpawnEnemyInterval)
                {
                    SpawnEnemy()
                    lastSpawnEnemyTimeSec = (System.currentTimeMillis() / 1000)
                    nextSpawnEnemyInterval = spawnIntervalSecList[Random.nextInt(0, spawnIntervalSecList.size)]
                }

                val timeUsed = System.currentTimeMillis() - start

                // 60 FPS
                val delayFor60FPS = (16.67 - timeUsed.toDouble()).coerceAtLeast(0.0) // bounded at 0
                delay(delayFor60FPS.toLong())
            }
        }
    }

    public fun cancelSpawnEnemy()
    {
        _runningChkAndSpawnEnemy = false
        lastSpawnEnemyTimeSec = 0L
        _enemySpawnJob?.cancel()
    }
}