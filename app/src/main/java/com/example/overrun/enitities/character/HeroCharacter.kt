package com.example.gohero.enitities.character

import com.example.gohero.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.gohero.enitities.GameConstant.DEFAULT_LIVES
import com.example.gohero.enitities.eCharacterType
import com.example.gohero.enitities.eHeroType

class HeroCharacter() :
    CharacterBase(eCharacterType.eHERO.toString(),
                    eCharacterType.eHERO, DEFAULT_LIVES, DEFAULT_HERO_SPEED)
{
    private var eType : eHeroType = eHeroType.eHERO_TOKAGE

    public fun setHeroType(eType : eHeroType){
        this.eType = eType
    }
    public fun getHeroType() = eType
}