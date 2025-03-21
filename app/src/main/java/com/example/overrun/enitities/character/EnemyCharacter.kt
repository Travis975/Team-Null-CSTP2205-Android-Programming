// EnemyCharacter.kt
package com.example.overrun.enitities.character

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.character.CharacterBase
import kotlin.math.sqrt

class EnemyCharacter(
    objectSizeManager: GameObjectSizeAndViewManager,
    private val hero: HeroCharacter
) : CharacterBase(
    id = "Enemy",
    etype = eCharacterType.eSLIME,
    lives = 1U,
    speed = 2U,
    objectSizeManager = objectSizeManager,
    interactable = false // Non-interactable
) {
    // Track precise position with Float and notify observers
    var xPos by mutableStateOf(getXPos().toFloat())
    var yPos by mutableStateOf(getYPos().toFloat())

    fun updatePositionTowardsHero() {
        val heroX = hero.getXPos().toFloat()
        val heroY = hero.getYPos().toFloat()

        val dx = heroX - xPos
        val dy = heroY - yPos
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 0) {
            val speed = getSpeed().toFloat()
            val moveX = (dx / distance) * speed
            val moveY = (dy / distance) * speed

            // Update precise Float positions
            xPos += moveX
            yPos += moveY

            // Update collider position (UInt) for collision detection
            updateXPos(xPos.toUInt())
            updateYPos(yPos.toUInt())
        }
    }
}