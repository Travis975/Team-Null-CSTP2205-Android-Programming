package com.example.overrun.enitities.gameStage

import androidx.compose.runtime.mutableStateOf

class GameMetricsAndControl {

    private var _heroHitCount = mutableStateOf(0U)
    private var _enemyKillCount = mutableStateOf(0U)
    private var _timeSurvived = mutableStateOf("0:00")

    // Pause Control
    var isGamePaused = mutableStateOf(false)

    // Remember the derived state in a composable scope
    fun getHeroHitCount() = _heroHitCount.value
    fun getEnemyKillCount() = _enemyKillCount.value
    fun getTimeSurvived() = _timeSurvived.value

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

    fun resetCounter()
    {
        _heroHitCount.value = 0U
        _enemyKillCount.value = 0U
        _timeSurvived.value = "0:00"
    }
}