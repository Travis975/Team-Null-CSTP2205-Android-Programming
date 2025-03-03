package com.example.gohero.enitities.character

import com.example.gohero.enitities.eCharacterType
import com.example.gohero.enitities.eDirection
import com.example.gohero.enitities.eObjectType
import com.example.overrun.enitities.GameObjectSizeManager
import com.example.overrun.enitities.gameobject.GameObject

// Character is a kind of GameObject
abstract class CharacterBase(id : String,
                             etype: eCharacterType,
                             lives : UInt, speed : UInt,
                             objectSizeManager : GameObjectSizeManager) :
    GameObject(id, eObjectType.eCHARACTER,
                objectSizeManager, true)
{

    // Character attributes
    private var _name : String = ""
    private var _speed : UInt = speed
    private var _eDir : eDirection = eDirection.eDOWN
    private var _etype : eCharacterType = etype
    private var _lives : UInt = lives

    // GameObject Base owns collider

    public fun setName(name : String) {
        _name = name
    }
    public fun getName() : String {
        return if (_name != "") _name else _etype.toString()
    }
    public fun setSpeed(speed: UInt) {
        _speed = speed
    }
    public fun getSpeed() = _speed

    public fun setLives(lives : UInt){
        _lives = lives
    }
    public fun getLives() = _lives
    public fun isDie() : Boolean = _lives <= 0U

    public fun setDirection(eDir: eDirection){
        _eDir = eDir
    }
    public fun getDirection() = _eDir

}