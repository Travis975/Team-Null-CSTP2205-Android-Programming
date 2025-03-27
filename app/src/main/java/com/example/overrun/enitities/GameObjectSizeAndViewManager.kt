package com.example.overrun.enitities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.overrun.enitities.GameConstant.DEFAULT_INTERACT_SIZE_EXTEND_RATIO
import com.example.overrun.enitities.GameConstant.DEFAULT_SCREEN_HEIGHT_PIXEL
import com.example.overrun.enitities.GameConstant.DEFAULT_SCREEN_WIDTH_PIXEL
import com.example.overrun.enitities.GameConstant.GAME_SCREEN_COL
import com.example.overrun.enitities.GameConstant.GAME_SCREEN_ROW
import com.example.overrun.enitities.character.HeroCharacter
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.eObjectType.*
import com.example.overrun.enitities.collider.Collider

class GameObjectSizeAndViewManager {

    private var _screenWidthPixel : UInt = DEFAULT_SCREEN_WIDTH_PIXEL
    private var _screenHeightPixel : UInt = DEFAULT_SCREEN_HEIGHT_PIXEL

    var screenWorldX by mutableStateOf(0f)
    var screenWorldY by mutableStateOf(0f)

    private var _currentStageMapRows : UInt = 0U
    private var _currentStageMapCols : UInt = 0U

    fun updateScreenSize(width : UInt, height : UInt){
        _screenWidthPixel = width
        _screenHeightPixel = height
    }
    fun getScreenWidth() = _screenWidthPixel
    fun getScreenHeight() = _screenHeightPixel

    fun InitScreenWorldXYPos(heroStartWorldX : UInt, heroStartWorldY : UInt, hero: HeroCharacter)
    {
        val heroHalfWidth = (hero.getCollider().getSizeWidth() / 2U)
        val heroHalfHeight = (hero.getCollider().getSizeHeight() / 2U)

        screenWorldX = (heroStartWorldX - (_screenWidthPixel / 2u) + heroHalfWidth).toFloat()
        screenWorldY = (heroStartWorldY - (_screenHeightPixel / 2u) + heroHalfHeight).toFloat()
    }

    fun updateCurrentMapRowColSize(row : UInt, col : UInt){
        _currentStageMapRows = row
        _currentStageMapCols = col
    }
    fun getCurrentStageRowCol() = Pair(_currentStageMapRows, _currentStageMapCols)

    public fun IsObjectInScreen(other : Collider) : Boolean{

        val otherXStart = other.getXPos().toFloat()
        val otherXEnd = other.getXEndPos().toFloat()
        val otherYStart = other.getYPos().toFloat()
        val otherYEnd = other.getYEndPos().toFloat()

        val halfScreenWidth = (_screenWidthPixel.toFloat() / 2f)
        val halfScreenHeight = (_screenHeightPixel.toFloat() / 2f)
        val screenXStart = screenWorldX - halfScreenWidth
        val screenYStart = screenWorldY - halfScreenHeight
        val screenXEnd = screenWorldX + (halfScreenWidth * 2f)
        val screenYEnd = screenWorldY + (halfScreenHeight * 3f)

        return screenXStart < otherXEnd && screenXEnd > otherXStart &&
                screenYStart < otherYEnd && screenYEnd > otherYStart
    }

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

    fun GET_ACTION_INTERACT_SIZE(objType : eObjectType) : UInt {
        var actionSizePx = 0U
        when(objType){
            // For characters, enemy:
            eCHARACTER, eENEMY -> {
                actionSizePx = GET_CHARACTER_INTERACT_SIZE() - GET_CHARACTER_SIZE()
            }

            // For all other listed objects:
//            eGRASS, eTREE_BACKGROUND,
//            eTREE, eTREE_28,
//            eTREE_YELLOW,
//            eROCK, eROCK_1,
//            eWALL, ePATH,
//            ePATH_RANDOM_3, ePATH_BLANK_MUD, ePATH_LEFT_BOUNDARY, ePATH_RIGHT_BOUNDARY,
//            ePATH_RANDOM, ePATH_RANDOM_2,
//            eMUSHROOMS,
//            eROCKY_PATCH,
//            eGRASS_BLANK, eGRASS_NORMAL, eGRASS_FLOWERS,
//            eWATER_TOP_CENTER, eWATER_TOP_LEFT, eWATER_TOP_RIGHT,
//            eWATER_BOTTOM_CENTER, eWATER_BOTTOM_LEFT, eWATER_CENTER,
//            eWATER_CENTER_LEFT, eWATER_CENTER_RIGHT, eWATER_LOW_RIGHT -> {
//                actionSizePx = (
//                        (DEFAULT_INTERACT_SIZE_EXTEND_RATIO - 1f)
//                                * GET_OBJECT_SIZE().toFloat()
//                        ).toUInt()
//            }

            // If it's not in the list (eNA or any unhandled case):
            else -> {}
        }
        return actionSizePx
    }

}