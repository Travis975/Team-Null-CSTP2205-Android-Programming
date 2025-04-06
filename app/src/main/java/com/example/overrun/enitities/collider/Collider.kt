package com.example.overrun.enitities.collider

import com.example.overrun.enitities.GameObjectSizeAndViewManager

class Collider(var id : String,
               var x : UInt, var y : UInt,
               var interactable : Boolean,
               var blockable : Boolean,
               var objectSizeAndViewManager : GameObjectSizeAndViewManager) {

    private val _objectID = id
    private var _xPos = x
    private var _yPos = y
    private var _objSizeAndViewManager = objectSizeAndViewManager
    // Object Collider default is true
    private var _isActive = true
    private var _isBlockable = blockable
    private val _isInteractable = interactable

    public fun getID() = _objectID
    public fun getXPos() = _xPos
    public fun getYPos() = _yPos
    public fun getXYPos() = Pair(_xPos, _yPos)
    public fun getSizeWidth() = _objSizeAndViewManager.GET_OBJECT_SIZE()
    public fun getSizeHeight() = _objSizeAndViewManager.GET_OBJECT_SIZE()
    public fun getXEndPos() = _xPos + getSizeWidth()
    public fun getYEndPos() = _yPos + getSizeHeight()
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _xPos = xPos
        _yPos = yPos
    }
    public fun updateXPos(xPos : UInt) {
        _xPos = xPos
    }
    public fun updateYPos(yPos : UInt) {
        _yPos = yPos
    }
    public fun updateXPosByDelta(xDelta : Float) {
        _xPos = (_xPos.toFloat() + xDelta).toUInt()
    }
    public fun updateYPosByDelta(yDelta : Float) {
        _yPos = (_yPos.toFloat() + yDelta).toUInt()
    }
    public fun setActive(flag : Boolean){
        _isActive = flag
    }

    public fun isActive() = _isActive
    public fun isBlockable() = _isBlockable
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
            blockable = this._isBlockable,
            objectSizeAndViewManager = this._objSizeAndViewManager
        )
    }


}