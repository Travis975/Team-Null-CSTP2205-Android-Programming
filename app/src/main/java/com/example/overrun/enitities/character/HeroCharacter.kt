package com.example.gohero.enitities.character

import com.example.gohero.enitities.GameConstant.DEFAULT_HERO_SPEED
import com.example.gohero.enitities.GameConstant.DEFAULT_LIVES
import com.example.gohero.enitities.eCharactertype

class HeroCharacter(pairStartPos : Pair<UInt, UInt>) :
    CharacterBase(eCharactertype.eHERO, pairStartPos, DEFAULT_LIVES, DEFAULT_HERO_SPEED)
{

}