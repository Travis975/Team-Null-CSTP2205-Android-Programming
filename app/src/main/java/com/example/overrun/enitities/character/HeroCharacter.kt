package com.example.overrun.enitities.character

import com.example.overrun.enitities.GameConstant
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.overrun.enitities.GameConstant.DEFAULT_LIVES
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.eHeroType
import com.example.overrun.enitities.eObjectType

class HeroCharacter(objectSizeManager : GameObjectSizeAndViewManager) :
    CharacterBase(eCharacterType.eHERO.toString(),
                    eCharacterType.eHERO, eObjectType.eCHARACTER,
                    DEFAULT_LIVES, DEFAULT_HERO_SPEED,
                    objectSizeManager)
{
    private var eType : eHeroType = eHeroType.eHERO_TOKAGE

    public fun setHeroType(eType : eHeroType){
        this.eType = eType
    }
    public fun getHeroType() = eType

    public fun getHeroXYPos() = getCollider().getXYPos()

    public fun reset(lives : UInt)
    {
        resetDieFinished()
        setLives(lives)
        getCollider().setActive(true)
    }
}