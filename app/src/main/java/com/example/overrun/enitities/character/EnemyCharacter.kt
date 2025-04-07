package com.example.overrun.enitities.character

import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_LIVES
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.eEnemyType
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.gameStage.EnemyConfiguration

class EnemyCharacter(config : EnemyConfiguration,
                     objectSizeManager : GameObjectSizeAndViewManager) :
    CharacterBase(config.id,
        eCharacterType.eENEMY, eObjectType.eENEMY,
        DEFAULT_ENEMY_LIVES, config.speed,
        objectSizeManager)
{
    private var eType : eEnemyType = config.eType
    private var _eWithObjType : eObjectType = config.eWithObjectType

    var runningMoveThread = true

    init{
        updatePosition(config.startX, config.startY)
    }

    public fun setHeroType(eType : eEnemyType){
        this.eType = eType
    }
    public fun getEnemyType() = eType
    public fun getWithObjectType() = _eWithObjType
}