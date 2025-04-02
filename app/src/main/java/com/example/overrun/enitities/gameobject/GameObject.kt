package com.example.overrun.enitities.gameobject

import androidx.compose.runtime.Immutable
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.collider.ActionCollider
import com.example.overrun.enitities.collider.Collider
import com.example.overrun.enitities.eDirection.*
import com.example.overrun.enitities.eObjectType

// notate as Immutable (stable type) for avoid unncessary recomposition
@Immutable
open class GameObject(id : String, objType : eObjectType,
                      objectSizeAndViewManager : GameObjectSizeAndViewManager,
                      interactable : Boolean,
                      x : UInt = 0U, y : UInt = 0U) {

    private val _objectType = objType
    private var _isDestroy = false

    // Object Collider, default world pos (0, 0)
    private var _collider = Collider(id, x, y, interactable,
                                        objectSizeAndViewManager)

    private var _actionCollider = mapOf(
        eDOWN to ActionCollider(_collider, eDOWN, _objectType, objectSizeAndViewManager),
        eUP to ActionCollider(_collider, eUP, _objectType, objectSizeAndViewManager),
        eLEFT to ActionCollider(_collider, eLEFT, _objectType, objectSizeAndViewManager),
        eRIGHT to ActionCollider(_collider, eRIGHT, _objectType, objectSizeAndViewManager)
    )

    public fun getID() = _collider.getID()
    public fun getObjType() = _objectType
    public fun getIsDestroy() = _isDestroy
    public fun getCollider() = _collider
    public fun getActionCollider() = _actionCollider

    public fun getXPos() = _collider.getXPos()
    public fun getYPos() = _collider.getYPos()
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _collider.updatePosition(xPos, yPos)
    }
    public fun updateXPosByDelta(xDelta : Float) {
        _collider.updateXPosByDelta(xDelta)
    }
    public fun updateYPosByDelta(yDelta : Float) {
        _collider.updateYPosByDelta(yDelta)
    }
    public fun updateXPos(xPos : UInt) {
        _collider.updateXPos(xPos)
    }
    public fun updateYPos(yPos : UInt) {
        _collider.updateYPos(yPos)
    }
    public fun setDestroy(){
        // When destroy deactivate collider
        _collider.setActive(false)
        _isDestroy = true
    }
}