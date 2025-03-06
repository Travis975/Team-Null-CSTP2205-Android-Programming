package com.example.overrun.enitities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.gohero.enitities.GameConstant.DEFAULT_INTERACT_SIZE_EXTEND_RATIO
import com.example.gohero.enitities.GameConstant.DEFAULT_SCREEN_HEIGHT_PIXEL
import com.example.gohero.enitities.GameConstant.DEFAULT_SCREEN_WIDTH_PIXEL
import com.example.gohero.enitities.GameConstant.GAME_SCREEN_COL
import com.example.gohero.enitities.GameConstant.GAME_SCREEN_ROW
import com.example.gohero.enitities.eObjectType
import com.example.gohero.enitities.eObjectType.*

class GameObjectSizeAndViewManager {

    private var _screenWidthPixel : UInt = DEFAULT_SCREEN_WIDTH_PIXEL
    private var _screenHeightPixel : UInt = DEFAULT_SCREEN_HEIGHT_PIXEL

    var screenWorldX by mutableStateOf(0f)
    var screenWorldY by mutableStateOf(0f)

    fun updateScreenSize(width : UInt, height : UInt){
        _screenWidthPixel = width
        _screenHeightPixel = height
    }
    fun getScreenWidth() = _screenWidthPixel
    fun getScreenHeight() = _screenHeightPixel

    fun GET_OBJECT_SIZE() : UInt
    {
        return minOf((_screenWidthPixel.toFloat() / GAME_SCREEN_COL).toUInt(),
                    (_screenHeightPixel.toFloat() / GAME_SCREEN_ROW).toUInt())
    }

    fun GET_CHARACTER_SIZE() : UInt
    {
        return GET_OBJECT_SIZE()
    }

    fun GET_CHARACTER_INTERACT_SIZE() : UInt
    {
        return (GET_CHARACTER_SIZE().toFloat() * DEFAULT_INTERACT_SIZE_EXTEND_RATIO).toUInt()
    }

    fun GET_ACTION_INTERACT_SIZE(objType : eObjectType) : UInt
    {
        var actionSizePx = 0U
        when(objType){
                     // Current all are the same
            eCHARACTER->{
                actionSizePx = GET_CHARACTER_INTERACT_SIZE() - GET_CHARACTER_SIZE()
            }
            eTREE, eWALL, eROCK, ePATH, eGRASS->{
                actionSizePx = ((DEFAULT_INTERACT_SIZE_EXTEND_RATIO - 1f) * GET_OBJECT_SIZE().toFloat()).toUInt()
            }
            else->{}
        }
        return actionSizePx
    }
}