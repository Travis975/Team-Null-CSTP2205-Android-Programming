package com.example.overrun.enitities.character

import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_SPEED
import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_LIVES
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.eEnemyType
import com.example.overrun.enitities.eCharacterType

class EnemyCharacter(eEnemy : eEnemyType,
                     startX : UInt, startY : UInt,
                     objectSizeManager : GameObjectSizeAndViewManager) :
    CharacterBase(eCharacterType.eENEMY.toString(),
        eCharacterType.eENEMY, DEFAULT_ENEMY_LIVES, DEFAULT_ENEMY_SPEED,
        objectSizeManager)
{
    private var eType : eEnemyType = eEnemy

    var runningMoveThread = true

    init{
        updatePosition(startX, startY)
    }

    public fun setHeroType(eType : eEnemyType){
        this.eType = eType
    }
    public fun getEnemyType() = eType
}