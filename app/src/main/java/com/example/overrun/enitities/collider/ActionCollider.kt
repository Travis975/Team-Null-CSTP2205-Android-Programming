package com.example.overrun.enitities.collider

import com.example.overrun.enitities.eDirection
import com.example.overrun.enitities.eDirection.*
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.GameObjectSizeAndViewManager

// ActionCollider is reference from the object collider x, y position
class ActionCollider(objectCollider: Collider, eDir: eDirection,
                      objType : eObjectType,
                      objectSizeAndViewManager : GameObjectSizeAndViewManager) {

    private var _objectCollider = objectCollider
    private var _eDir = eDir
    // eCHARACTER, eTREE, eWALL, eROCK, ePATH, eGRASS
    private var _objType = objType
    private var _objSizeAndViewManager = objectSizeAndViewManager
    // Action Collider default is false
    private var _isActive = false

    public fun getID() = _objectCollider.getID()

    public fun setActive(flag : Boolean){
        _isActive = flag
    }
    public fun isActive() = _isActive

    public fun getSizeWidth() : UInt{
        when(_eDir){
            eUP, eDOWN->{
                return _objectCollider.getSizeWidth()
            }
            eLEFT, eRIGHT->{
                return _objSizeAndViewManager.GET_ACTION_INTERACT_SIZE(_objType)
            }
        }
    }
    public fun getSizeHeight() : UInt{
        when(_eDir){
            eUP, eDOWN->{
                return _objSizeAndViewManager.GET_ACTION_INTERACT_SIZE(_objType)
            }
            eLEFT, eRIGHT->{
                return _objectCollider.getSizeHeight()
            }
        }
    }

    public fun getXPos() : UInt {
        when(_eDir){
            eUP, eDOWN ->{
                // for UP or DOWN, X is the same as the object itself
                return _objectCollider.getXPos()
            }
            eLEFT->{
                // Object top left corner X - (move left)the action collider size
                return _objectCollider.getXPos() - getSizeWidth()
            }
            eRIGHT->{
                // use the object top right corner
                return _objectCollider.getXEndPos()
            }
            else->{
                return 0U
            }
        }
    }

    public fun getYPos() : UInt {
        when(_eDir){
            eUP ->{
                // Object top left corner Y - the action size
                return _objectCollider.getYPos() - getSizeHeight()
            }
            eDOWN->{
                // Object bottom left corner
                return _objectCollider.getYEndPos()
            }
            eLEFT, eRIGHT->{
                // for LEFT or RIGHT, Y is the same as the object itself
                return _objectCollider.getYPos()
            }
            else->{
                return 0U
            }
        }
    }

    public fun getXEndPos() = getXPos() + getSizeWidth()
    public fun getYEndPos() = getYPos() + getSizeHeight()

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    public fun IsCollided(other : Collider, offsetX: Int = 0, offsetY: Int = 0) : Boolean{

        val otherXStart = (other.getXPos().toInt() - offsetX).toUInt()
        val otherXEnd = (other.getXEndPos().toInt() + offsetX).toUInt()
        val otherYStart = (other.getYPos().toInt() - offsetY).toUInt()
        val otherYEnd = (other.getYEndPos().toInt() + offsetY).toUInt()

        return this.getXPos() < otherXEnd && this.getXEndPos() > otherXStart &&
                this.getYPos() < otherYEnd && this.getYEndPos() > otherYStart
    }
}