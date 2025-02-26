package com.example.overrun.enitities

import com.example.gohero.enitities.GameConstant.DEFAULT_INTERACT_SIZE_EXTEND_RATIO
import com.example.gohero.enitities.GameConstant.DEFAULT_SCREEN_HEIGHT_PIXEL
import com.example.gohero.enitities.GameConstant.DEFAULT_SCREEN_WIDTH_PIXEL
import com.example.gohero.enitities.GameConstant.GAME_SCREEN_COL
import com.example.gohero.enitities.GameConstant.GAME_SCREEN_ROW

class GameObjectSizeManager {

    var screenWidthPixel : UInt = DEFAULT_SCREEN_WIDTH_PIXEL
    var screenHeightPixel : UInt = DEFAULT_SCREEN_HEIGHT_PIXEL

    fun updateScreenSize(width : UInt, height : UInt){
        screenWidthPixel = width
        screenHeightPixel = height
    }

    fun GET_OBJECT_SIZE() : UInt
    {
        return minOf((screenWidthPixel.toFloat() / GAME_SCREEN_COL).toUInt(),
                    (screenHeightPixel.toFloat() / GAME_SCREEN_ROW).toUInt())
    }

    fun GET_CHARACTER_SIZE() : UInt
    {
        return GET_OBJECT_SIZE()
    }

    fun GET_CHARACTER_INTERACT_SIZE() : UInt
    {
        return (GET_CHARACTER_SIZE().toFloat() * DEFAULT_INTERACT_SIZE_EXTEND_RATIO).toUInt()
    }
}