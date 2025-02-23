package com.example.overrun.enitities.collider

import com.example.gohero.enitities.GameConstant.MOVE_COLLIDE_OFFSET_X
import com.example.gohero.enitities.GameConstant.MOVE_COLLIDE_OFFSET_Y
import com.example.gohero.enitities.character.HeroCharacter
import com.example.overrun.enitities.gameobject.GameObject

class ColliderManager {

    // use Hash Map for memory and speed since objects order not a matter
    private var _heroCollider : Collider? = null
    private var _enemyColliders : MutableList<Collider> = mutableListOf()
    private var _objectColliders : MutableList<Collider> = mutableListOf()

    public fun resetAllCollider()
    {
        _heroCollider = null
        _enemyColliders.clear()
        _objectColliders.clear()
    }

    public fun setHeroCollider(hero : HeroCharacter)
    {
        _heroCollider = hero.getCollider()
    }
    public fun getHeroCollider() = _heroCollider

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
                if (heroCollider.IsCollided(otherCollider, offsetX, offsetY))
                {
                    return otherCollider.getID()
                }
            }
        }
        return null
    }
}