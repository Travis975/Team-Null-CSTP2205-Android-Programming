package com.example.overrun.enitities.gameStage

import androidx.compose.runtime.mutableStateOf

class GameMetricsAndControl {

    private var _heroHitCount = mutableStateOf(0U)
    private var _enemyKillCount = mutableStateOf(0U)

    // Pause Control
    var isGamePaused = mutableStateOf(false)

    // Remember the derived state in a composable scope
    fun getHeroHitCount() = _heroHitCount.value
    fun getEnemyKillCount() = _enemyKillCount.value

    fun addHeroHitCount()
    {
        _heroHitCount.value++
    }

    fun addEnemyKillCount()
    {
        _enemyKillCount.value++
    }

    fun resetCounter()
    {
        _heroHitCount.value = 0U
        _enemyKillCount.value = 0U
    }
}