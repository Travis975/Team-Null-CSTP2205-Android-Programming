package com.example.overrun.enitities.collider

import com.example.overrun.enitities.GameObjectSizeManager

class Collider(var id : String,
               var x : UInt, var y : UInt,
               var interactable : Boolean,
               var objectSizeManager : GameObjectSizeManager) {

    private val _objectID = id
    private var _xPos = x
    private var _yPos = y
    private var _objSizeManager = objectSizeManager
    // Object Collider default is true
    private var _isActive = true
    private val _isInteractable = interactable

    public fun getID() = _objectID
    public fun getXPos() = _xPos
    public fun getYPos() = _yPos
    public fun getSizeWidth() = _objSizeManager.GET_OBJECT_SIZE()
    public fun getSizeHeight() = _objSizeManager.GET_OBJECT_SIZE()
    public fun getXEndPos() = _xPos + getSizeWidth()
    public fun getYEndPos() = _yPos + getSizeHeight()
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _xPos = xPos
        _yPos = yPos
    }
    public fun setActive(flag : Boolean){
        _isActive = flag
    }
    public fun isActive() = _isActive
    public fun isInteractable() = _isInteractable

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    public fun IsCollided(other : Collider, offsetX: Int = 0, offsetY: Int = 0) : Boolean{

        val otherXStart = (other.getXPos().toInt() - offsetX).toUInt()
        val otherXEnd = (other.getXEndPos().toInt() + offsetX).toUInt()
        val otherYStart = (other.getYPos().toInt() - offsetY).toUInt()
        val otherYEnd = (other.getYEndPos().toInt() + offsetY).toUInt()

        return this._xPos < otherXEnd && this.getXEndPos() > otherXStart &&
                this._yPos < otherYEnd && this.getYEndPos() > otherYStart
    }

    fun copyInstance(): Collider {
        return Collider(
            id = this._objectID,
            x = this._xPos,
            y = this._yPos,
            interactable = this._isInteractable,
            objectSizeManager = this._objSizeManager
        )
    }
}