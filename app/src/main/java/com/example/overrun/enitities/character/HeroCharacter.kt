package com.example.gohero.enitities.character

import com.example.gohero.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.gohero.enitities.GameConstant.DEFAULT_LIVES
import com.example.gohero.enitities.eCharactertype
import com.example.gohero.enitities.eHerotype
import com.example.gohero.enitities.eHerotype.*

class HeroCharacter() :
    CharacterBase(eCharactertype.eHERO, DEFAULT_LIVES, DEFAULT_HERO_SPEED)
{
    private var eType : eHerotype = eHerotype.eHERO_TOKAGE

    public fun setHeroType(eType : eHerotype){
        this.eType = eType
    }
    public fun getHeroType() = eType
}