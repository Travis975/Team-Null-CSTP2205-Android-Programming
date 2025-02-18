package com.example.gohero.enitities.character

import androidx.lifecycle.ViewModel
import com.example.gohero.enitities.eCharactertype
import com.example.gohero.enitities.eDirection

abstract class CharacterBase(etype: eCharactertype,
                             lives : UInt, speed : UInt)

    // Important, use viewModel parent to presist the character state across the composable screen
    // and having the game life-cycle
    // : ViewModel()
{

    // Character attributes
    private var _name : String = ""
    private var _speed : UInt = speed
    private var _eDir : eDirection = eDirection.eDOWN
    private var _etype : eCharactertype = etype
    private var _lives : UInt = lives

    // Character Position, need to be animated for smooth transition
    private var _xPos = 0U
    private var _yPos = 0U

    // TO DO:
    // private var spriteObj : CharacterSprite

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

    public fun getXPos() = _xPos
    public fun getYPos() = _yPos
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _xPos = xPos
        _yPos = yPos
    }

    public fun setDirection(eDir: eDirection){
        _eDir = eDir
    }
    public fun getDirection() = _eDir

}