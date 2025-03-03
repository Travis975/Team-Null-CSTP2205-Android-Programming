package com.example.overrun.enitities.gameobject

import com.example.gohero.enitities.eDirection
import com.example.gohero.enitities.eDirection.*
import com.example.gohero.enitities.eObjectType
import com.example.overrun.enitities.GameObjectSizeManager
import com.example.overrun.enitities.collider.ActionCollider
import com.example.overrun.enitities.collider.Collider

open class GameObject(id : String, objType : eObjectType,
                      objectSizeManager : GameObjectSizeManager,
                      interactable : Boolean,
                      x : UInt = 0U, y : UInt = 0U) {

    private val _objectType = objType
    private var _isDestroy = false

    // Object Collider, default pos (0, 0)
    private var _collider = Collider(id, x, y, interactable,
                                     objectSizeManager)

    private var _actionCollider = mapOf(
        eDOWN to ActionCollider(_collider, eDOWN, _objectType, objectSizeManager),
        eUP to ActionCollider(_collider, eUP, _objectType, objectSizeManager),
        eLEFT to ActionCollider(_collider, eLEFT, _objectType, objectSizeManager),
        eRIGHT to ActionCollider(_collider, eRIGHT, _objectType, objectSizeManager)
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
    public fun setDestroy(){
        // When destroy deactivate collider
        _collider.setActive(false)
        _isDestroy = true
    }
}