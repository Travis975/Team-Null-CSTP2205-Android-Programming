package com.example.overrun.enitities.character

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.overrun.enitities.eCharacterType
import com.example.overrun.enitities.eDirection
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.gameobject.GameObject

// Character is a kind of GameObject
abstract class CharacterBase(id : String,
                             etype: eCharacterType,
                             objType : eObjectType,
                             lives : UInt, speed : UInt,
                             objectSizeManager : GameObjectSizeAndViewManager) :
    GameObject(id, objType,
                objectSizeManager, true)
{

    // Character attributes
    private var _name : String = ""
    private var _speed : UInt = speed
    private var _eDir : eDirection = eDirection.eDOWN
    private var _etype : eCharacterType = etype
    private var _lives : MutableState<UInt> = mutableStateOf(lives)
    private var _finishedDie = false

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

    public fun setLives(lives: UInt) {
        _lives.value = lives
    }
    public fun decrementLives(size: UInt) {
        _lives.value = (_lives.value - size).coerceAtLeast(0U)
    }
    public fun getLives() = _lives.value

    public fun isDie() : Boolean = _lives.value <= 0U
    public fun setDieFinished()
    {
        _finishedDie = true
    }
    public fun isDieFinished() = _finishedDie

    public fun setDirection(eDir: eDirection){
        _eDir = eDir
    }
    public fun getDirection() = _eDir

}