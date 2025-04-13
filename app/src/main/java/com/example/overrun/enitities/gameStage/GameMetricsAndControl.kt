package com.example.overrun.enitities.gameStage

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.example.overrun.enitities.eEnemyType
import kotlin.collections.set

class GameMetricsAndControl {

    private var _heroHitCount = mutableStateOf(0U)
    private var _enemyKillCount = mutableStateOf(0U)
    private var _stageEnemySize = mutableStateOf(0U)
    private var _timeSurvived = mutableStateOf("0:00")

    private var _mapEnemyTypeToImageBitmap = mutableMapOf<eEnemyType, ImageBitmap>()
    fun getEnemyTypeImage(eType: eEnemyType) : ImageBitmap ? {

        return _mapEnemyTypeToImageBitmap[eType]
    }

    fun addEnemyImageToMetric(eType: eEnemyType, bitmap: ImageBitmap)
    {
        if (!_mapEnemyTypeToImageBitmap.contains(eType))
        {
            _mapEnemyTypeToImageBitmap[eType] = bitmap
        }
    }

    // Pause Control
    var isGamePaused = mutableStateOf(false)

    // Remember the derived state in a composable scope
    fun getHeroHitCount() = _heroHitCount.value
    fun getEnemyKillCount() = _enemyKillCount.value
    fun getTimeSurvived() = _timeSurvived.value
    fun getEnemySize() = _stageEnemySize.value
    fun getEnemyRemain() = (_stageEnemySize.value - _enemyKillCount.value).coerceAtLeast(0U)
    fun isStageClear() : Boolean{
        return _stageEnemySize.value != 0U &&
                _enemyKillCount.value != 0U &&
                getEnemyRemain() <= 0U
    }

    fun addHeroHitCount()
    {
        _heroHitCount.value++
    }

    fun addEnemyKillCount()
    {
        _enemyKillCount.value++
    }
    fun setTimeSurvived(time: String) {
        _timeSurvived.value = time // Setter for time survived
    }
    fun setStageEnemySize(size : UInt)
    {
        _stageEnemySize.value = size
    }

    fun resetCounter()
    {
        _heroHitCount.value = 0U
        _enemyKillCount.value = 0U
        _stageEnemySize.value = 0U
        _timeSurvived.value = "0:00"
    }
}