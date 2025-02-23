package com.example.overrun.enitities.collider

class Collider(var id : String,
               var x : UInt, var y : UInt,
               var width : UInt, var height : UInt) {

    private val _objectID = id
    private var _xPos = x
    private var _yPos = y
    private var _width = width
    private var _height = height

    public fun getID() = _objectID
    public fun getXPos() = _xPos
    public fun getYPos() = _yPos
    public fun getXEndPos() = _xPos + _width
    public fun getYEndPos() = _yPos + _height
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _xPos = xPos
        _yPos = yPos
    }

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
            width = this._width,
            height = this._height,
            x = this._xPos,
            y = this._yPos
        )
    }
}