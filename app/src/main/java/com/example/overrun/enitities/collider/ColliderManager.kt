package com.example.overrun.enitities.collider

import com.example.gohero.enitities.GameConstant.MOVE_COLLIDE_OFFSET_X
import com.example.gohero.enitities.GameConstant.MOVE_COLLIDE_OFFSET_Y
import com.example.gohero.enitities.character.HeroCharacter
import com.example.gohero.enitities.eDirection
import com.example.overrun.enitities.gameobject.GameObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.gohero.enitities.GameConstant.INTERACT_FILER_INTERVAL_MS

class ColliderManager {

    // use Hash Map for memory and speed since objects order not a matter
    private var _heroCollider : Collider? = null
    // A map of four collider
    private var _heroActionCollider : Map<eDirection, ActionCollider> = mapOf()
    private var _enemyColliders : MutableList<Collider> = mutableListOf()
    private var _objectColliders : MutableList<Collider> = mutableListOf()

    // Single Thread Instance for hero to object collision detection
    private var _actionCollisionJob : Job? = null
    private var _runningActionCollisionChk = true
    private val _coroutineScope = CoroutineScope(Dispatchers.Default)
    private var _heroInteractedToOther = mutableStateMapOf<String, Long>() // [objectID, timeStamp]
    public val heroInteractedToOther : SnapshotStateMap<String, Long> = _heroInteractedToOther

    public fun resetAllCollider()
    {
        _heroCollider = null
        _heroActionCollider = mapOf()
        _enemyColliders.clear()
        _objectColliders.clear()
    }

    public fun setHeroCollider(hero : HeroCharacter)
    {
        _heroCollider = hero.getCollider()
        _heroActionCollider = hero.getActionCollider()
    }
    public fun getHeroCollider() = _heroCollider
    public fun getHeroActionCollider() = _heroActionCollider

//    public fun setEnemyColliders(enemies : MutableList<EnemyCharacter>)
//    {
//        enemyColliders.clear()
//        enemyColliders = enemies.map{ it.getCollider() }.toMutableList()
//
//    }

    public fun setObjectColliders(gameObjects : MutableList<GameObject>)
    {
        _objectColliders.clear()
        _objectColliders = gameObjects.map{ it.getCollider() }.toMutableList()
    }

    // Return Object ID String for the first blocked
    // Return null for no block
    public fun detectMoveCollision(xDelta : Int, yDelta : Int) : String?
    {
        if (_heroCollider != null &&
            _objectColliders.isNotEmpty()) {

            val futureHeroCollider = _heroCollider?.copyInstance()

            if (futureHeroCollider != null)
            {
                val newXPos = futureHeroCollider.getXPos() + xDelta.toUInt()
                val newYPos = futureHeroCollider.getYPos() + yDelta.toUInt()
                futureHeroCollider.updatePosition(newXPos, newYPos)

                return detectCollision(heroCollider = futureHeroCollider,
                                        otherColliders = _objectColliders,
                                        offsetX = MOVE_COLLIDE_OFFSET_X,
                                        offsetY = MOVE_COLLIDE_OFFSET_Y)
            }
        }
        return null
    }

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    public fun detectCollision(heroCollider : Collider, otherColliders : List<Collider>,
                               offsetX: Int = 0, offsetY: Int = 0) : String?
    {
        if (otherColliders.isNotEmpty()) {

            otherColliders.forEach{ otherCollider ->

                // Allow hero to move towards the object a little before blocking
                if (otherCollider.isActive() && // when object is still not destroyed
                    heroCollider.IsCollided(otherCollider, offsetX, offsetY))
                {
                    return otherCollider.getID()
                }
            }
        }
        return null
    }

    public fun detectHeroActionCollision(otherCollider : Collider,
                                         offsetX: Int = 0, offsetY: Int = 0) : String? {
        if (_heroActionCollider.isNotEmpty())
        {
            _heroActionCollider.forEach{ (eDir, actionCollider)->

                // when action isActive (attacking)
                if (actionCollider.isActive() &&
                    actionCollider.IsCollided(otherCollider, offsetX, offsetY))
                {
                    return otherCollider.getID()
                }
            }
        }
        return null
    }

    private fun checkHeroActionToOtherColliders()
    {
        if (_heroActionCollider.isNotEmpty())
        {
            // Check To Enemy

            // Check To Object
            if (_objectColliders.isNotEmpty())
            {
                _objectColliders.forEach{ objectCollider->

                    val objID = objectCollider.getID()

                    // if object is still active, not yet destroyed
                    if (objectCollider.isActive() &&
                        objectCollider.isInteractable())
                    {
                        val lastInteractTime = _heroInteractedToOther[objID]
                        val curTime = System.currentTimeMillis()
                        // either the first time Or the interval larger than define value
                        val allowInteract = lastInteractTime == null ||
                                            ((curTime - lastInteractTime) > INTERACT_FILER_INTERVAL_MS.toLong())
                        if (allowInteract &&
                            detectHeroActionCollision(objectCollider) != null)
                        {
                            _heroInteractedToOther[objID] = curTime
                        }
                    }
                }
            }
        }
    }
    public fun startHeroActionCollisionCheck()
    {
        // Cancel if had previous coroutine
        cancelHeroActionCollisionCheck()
        _runningActionCollisionChk = true

        // Then Launch a new coroutine
        _actionCollisionJob = _coroutineScope.launch{
            while(_runningActionCollisionChk)
            {
                checkHeroActionToOtherColliders()
                // 60 FPS
                delay(16L)
            }
        }
    }
    public fun cancelHeroActionCollisionCheck()
    {
        _heroInteractedToOther.clear()
        _runningActionCollisionChk = false
        _actionCollisionJob?.cancel()
    }
}