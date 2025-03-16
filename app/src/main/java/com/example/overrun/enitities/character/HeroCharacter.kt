package com.example.overrun.enitities.character

import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.overrun.enitities.GameConstant.DEFAULT_LIVES
import com.example.overrun.enitities.character.CharacterBase
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.eHeroType
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.gameStage.GameMetrics

class HeroCharacter(objectSizeManager : GameObjectSizeAndViewManager) :
    CharacterBase(eCharacterType.eHERO.toString(),
                    eCharacterType.eHERO, DEFAULT_LIVES, DEFAULT_HERO_SPEED,
                    objectSizeManager)
{
    private var eType : eHeroType = eHeroType.eHERO_TOKAGE

    public fun setHeroType(eType : eHeroType){
        this.eType = eType
    }
    public fun getHeroType() = eType
}