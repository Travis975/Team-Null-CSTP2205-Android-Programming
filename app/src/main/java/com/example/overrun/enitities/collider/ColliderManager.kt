package com.example.overrun.enitities.collider

import com.example.overrun.enitities.GameConstant.MOVE_COLLIDE_OFFSET_X
import com.example.overrun.enitities.GameConstant.MOVE_COLLIDE_OFFSET_Y
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.eDirection
import com.example.overrun.enitities.gameobject.GameObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.overrun.enitities.GameConstant.BE_INTERACT_COLLIDE_OFFSET_X
import com.example.overrun.enitities.GameConstant.BE_INTERACT_COLLIDE_OFFSET_Y
import com.example.overrun.enitities.GameConstant.INTERACT_FILER_INTERVAL_MS
import com.example.overrun.enitities.GameViewModel
import com.example.overrun.enitities.character.EnemyCharacter
import java.util.concurrent.CopyOnWriteArrayList

class ColliderManager (private val gameViewModel: GameViewModel) {

    // use Hash Map for memory and speed since objects order not a matter
    private var _heroCollider : Collider? = null
    // A map of four collider
    private var _heroActionCollider : Map<eDirection, ActionCollider> = mapOf()

    // Thread-safe collections for concurrent access
    private val _enemyColliders = CopyOnWriteArrayList<Collider>()
    private val _objectColliders = CopyOnWriteArrayList<Collider>()

    // Single Thread Instance for hero to object collision detection
    private var _actionCollisionJob : Job? = null
    private var _runningActionCollisionChk = true
    private val _coroutineScope = CoroutineScope(Dispatchers.Default)

    private var _heroInteractedToOther = mutableStateMapOf<String, Long>() // [objectID, timeStamp]
    public val heroInteractedToOther : SnapshotStateMap<String, Long> = _heroInteractedToOther

    private var _otherInteractedToHero = mutableStateOf(Pair(" ", 0L)) // Pair[objectID, timeStamp]
    public val otherInteractedToHero : Pair<String, Long> get() = _otherInteractedToHero.value

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

    public fun setObjectColliders(gameObjects : MutableList<GameObject>)
    {
        // Create thread-safe copy of colliders
        _objectColliders.clear()
        _objectColliders.addAll(gameObjects.map { it.getCollider() })
    }

    public fun addObjectCollider(gameObject : GameObject)
    {
        // Synchronized add to prevent concurrent modification
        synchronized(_objectColliders) {
            _objectColliders.add(gameObject.getCollider())
        }
    }

    // Return Object ID String for the first blocked
    // Return null for no block
    public fun detectMoveCollision(xDelta : Int, yDelta : Int) : String?
    {
        if (_heroCollider != null && _objectColliders.isNotEmpty()) {

            // Create temporary collider for prediction
            val futureHeroCollider = _heroCollider?.copyInstance()

            if (futureHeroCollider != null)
            {
                val newXPos = futureHeroCollider.getXPos() + xDelta.toUInt()
                val newYPos = futureHeroCollider.getYPos() + yDelta.toUInt()
                futureHeroCollider.updatePosition(newXPos, newYPos)

                // Use snapshot list for thread-safe collision detection
                return detectCollision(
                    heroCollider = futureHeroCollider,
                    otherColliders = _objectColliders.toList(),
                    offsetX = MOVE_COLLIDE_OFFSET_X,
                    offsetY = MOVE_COLLIDE_OFFSET_Y
                )
            }
        }
        return null
    }

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    public fun detectCollision(
        heroCollider : Collider,
        otherColliders : List<Collider>,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) : String?
    {
        // Iterate over snapshot list
        otherColliders.forEach { otherCollider ->
            // Allow hero to move towards the object a little before blocking
            if (otherCollider.isActive() &&
                heroCollider.IsCollided(otherCollider, offsetX, offsetY))
            {
                return otherCollider.getID()
            }
        }
        return null
    }

    public fun detectHeroActionCollision(
        otherCollider : Collider,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) : String? {
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

    private fun checkIfHeroAndOtherCollidersCollides()
    {
        // Create snapshots for thread-safe iteration
        val objectColliders = _objectColliders.toList()
        val enemyColliders = _enemyColliders.toList()

        // Check To Enemy
        if (enemyColliders.isNotEmpty())
        {
            // Enemy collision logic placeholder
        }

        // Check To Object
        if (objectColliders.isNotEmpty())
        {
            objectColliders.forEach { objectCollider ->
                val objID = objectCollider.getID()

                // if object is still active, not yet destroyed
                if (objectCollider.isActive() && objectCollider.isInteractable())
                {
                    val lastHeroInteractTime = _heroInteractedToOther[objID]  // For Hero Interact Other
                    val lastOtherInteractTime = _otherInteractedToHero.value.second  // For Other Interact Hero

                    val curTime = System.currentTimeMillis()

                    // either the first time Or the interval larger than define value
                    val allowHeroInteract = lastHeroInteractTime == null ||
                            ((curTime - lastHeroInteractTime) > INTERACT_FILER_INTERVAL_MS.toLong())

                    val allowOtherInteract = lastOtherInteractTime == 0L ||
                            ((curTime - lastOtherInteractTime) > INTERACT_FILER_INTERVAL_MS.toLong())

                    if (allowHeroInteract && detectHeroActionCollision(objectCollider) != null)
                    {
                        _heroInteractedToOther[objID] = curTime
                    }

                    val heroCollider = _heroCollider

                    if (allowOtherInteract &&
                        heroCollider != null &&
                        heroCollider.isActive() &&  // not at interacting
                        heroCollider.IsCollided(objectCollider, BE_INTERACT_COLLIDE_OFFSET_X, BE_INTERACT_COLLIDE_OFFSET_Y))
                    {
                        _otherInteractedToHero.value = Pair(objID, curTime)
                    }
                }
            }
        }
    }

    public fun cancelCollisionCheck() {
        _heroInteractedToOther.clear()
        _runningActionCollisionChk = false
        _actionCollisionJob?.cancel()
    }

    public fun startCollisionCheck() {
        // Cancel if had previous coroutine
        cancelCollisionCheck()
        _runningActionCollisionChk = true

        // Then Launch a new coroutine
        _actionCollisionJob = _coroutineScope.launch {
            while (_runningActionCollisionChk) {
                val start = System.currentTimeMillis()
                checkIfHeroAndOtherCollidersCollides()

                // Maintain ~60 FPS timing
                val timeUsed = System.currentTimeMillis() - start
                val delayFor60FPS = (16.67 - timeUsed).coerceAtLeast(0.0)
                delay(delayFor60FPS.toLong())
            }
        }
    }

    // Enemy collider management
    public fun addEnemyCollider(enemy: EnemyCharacter) {
        synchronized(_enemyColliders) {
            _enemyColliders.add(enemy.getCollider())
        }
    }

    public fun removeEnemyCollider(enemy: EnemyCharacter) {
        synchronized(_enemyColliders) {
            _enemyColliders.remove(enemy.getCollider())
        }
    }
}