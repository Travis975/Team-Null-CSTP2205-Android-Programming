package com.example.overrun.enitities.gameStage

import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_SPEED
import com.example.overrun.enitities.eEnemyType
import com.example.overrun.enitities.eObjectType

// define enemy configure data class
data class EnemyConfiguration(var id : String = "",
                              val eType : eEnemyType,
                              var eWithObjectType: eObjectType = eObjectType.eNA,
                              var speed: UInt = DEFAULT_ENEMY_SPEED,
                              var startX : UInt = 0U, var startY : UInt = 0U)
