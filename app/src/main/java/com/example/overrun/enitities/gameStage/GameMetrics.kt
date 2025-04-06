package com.example.overrun.enitities.gameStage

import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicInteger

class GameMetrics {

    private var _heroHitCount = mutableStateOf(0U) // AtomicInteger(0)

    // Remember the derived state in a composable scope
    fun getHeroHitCount() = _heroHitCount.value

    fun addHeroHitCount()
    {
        _heroHitCount.value++
    }
}