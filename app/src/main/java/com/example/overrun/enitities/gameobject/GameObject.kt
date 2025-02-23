package com.example.overrun.enitities.gameobject

import com.example.gohero.enitities.eObjectType
import com.example.overrun.enitities.collider.Collider

open class GameObject(id : String, objType : eObjectType,
                      width: UInt, height: UInt,
                      x : UInt = 0U, y : UInt = 0U) {

    private val _objectType = objType

    // Object Collider, default pos (0, 0)
    private var _collider = Collider(id, x, y,
                                    width = width,
                                    height = height)

    public fun getID() = _collider.getID()
    public fun getObjType() = _objectType
    public fun getCollider() = _collider

    public fun getXPos() = _collider.getXPos()
    public fun getYPos() = _collider.getYPos()
    public fun updatePosition(xPos : UInt, yPos : UInt) {
        _collider.updatePosition(xPos, yPos)
    }
}