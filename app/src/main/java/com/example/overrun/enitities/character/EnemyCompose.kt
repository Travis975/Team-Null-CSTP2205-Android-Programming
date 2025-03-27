package com.example.overrun.enitities.character

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.overrun.R
import com.example.overrun.control.DrawDragDirectionArrow
import com.example.overrun.control.DrawTapCircle
import com.example.overrun.control.GuestureControllerEx
import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_HURT_INVINCIBLE_CYCLE
import com.example.overrun.enitities.GameConstant.DEFAULT_ENEMY_REPEL_SPEED
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_HURT_INVINCIBLE_CYCLE
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_REPEL_SPEED
import com.example.overrun.enitities.GameConstant.ENEMY_CHARACTER_SPRITE_HEIGHT_PIXEL
import com.example.overrun.enitities.GameConstant.ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.collider.ColliderManager.eColliderType
import com.example.overrun.enitities.eDirection
import com.example.overrun.enitities.eDirection.eDOWN
import com.example.overrun.enitities.eDirection.eLEFT
import com.example.overrun.enitities.eDirection.eRIGHT
import com.example.overrun.enitities.eDirection.eUP
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.sprites.loadSpriteSheet
import com.example.overrun.enitities.sprites.loadSpriteSheet1D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EnemyCompose(enemy : CharacterBase,
                 getHeroXYPos: ()->Pair<UInt, UInt>,
                colliderManager: ColliderManager,
                objectSizeAndViewManager : GameObjectSizeAndViewManager) {

    // ********************* Temp Remark, enemy do not have action collider
    // For Debugging
    //val bFlagDisplayActionCollider = false
    var isEnemyDie by remember { mutableStateOf<Boolean>(enemy.isDie()) }
    if (isEnemyDie)
    {
        return
    }

    // Change to use World Coord for Screen X Y Pos system for trigger rendering
    // User animateFLoat for smooth transition of value
    val xScreenPos by animateFloatAsState(objectSizeAndViewManager.screenWorldX, label="ScreenXPos")
    val yScreenPos by animateFloatAsState(objectSizeAndViewManager.screenWorldY, label="ScreenYPos")

    var xPosEnemy by remember { mutableStateOf<UInt>(enemy.getXPos()) }
    var yPosEnemy by remember { mutableStateOf<UInt>(enemy.getYPos()) }

    // An Async handler
    var currentMoveJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current
    val density = LocalDensity.current

    // Control and Render
    val startMove = remember{mutableStateOf(true)} // default enemy always move, even pos not change, sprite keep changes

    val startAttack = remember{mutableStateOf(false)}
    val isAttacking = remember{mutableStateOf(false)}
    val lastAttackDir = remember{ mutableStateOf(enemy.getDirection()) }


    // Enemy is a kind of Character
    val CHARACTER_SIZE = objectSizeAndViewManager.GET_CHARACTER_SIZE()
    val CHARACTER_INTERACT_EXTEND_SIZE = objectSizeAndViewManager.GET_CHARACTER_INTERACT_SIZE()

    // Load Sprite Once
    // Also store the bitmap resource to prevent recreation during rendering
    val spriteMove = remember{
        loadSpriteSheet(context.resources, (enemy as EnemyCharacter).getEnemyType().resId,
            ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL, ENEMY_CHARACTER_SPRITE_HEIGHT_PIXEL,      // 144 x 144 pixels, it related to the .png
            // would do a auto sprite scale up or down from the screen size
            ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL.toFloat() / objectSizeAndViewManager.GET_CHARACTER_SIZE().toFloat()
        )
    }
    val spriteDie = remember{
        loadSpriteSheet1D(context.resources, R.drawable.enemy_die_sprite,
            ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL, ENEMY_CHARACTER_SPRITE_HEIGHT_PIXEL,
            // would do a auto sprite scale up or down from the screen size
            ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL.toFloat() / objectSizeAndViewManager.GET_CHARACTER_SIZE().toFloat()
        )
    }

    // Default Down
    val currentSprite = remember{ mutableStateOf(spriteMove[eDirection.eDOWN.value][0]) }

    var lastDieSpriteFrameIndex by remember{ mutableStateOf<Int>(-1) } // default not started

    val lastMoveSpriteFrameIndex = remember{ mutableStateOf(0) }

    // ********************** Temp Remark do not have attack sprite for enemy
//    val spriteAttack = remember{
//        hashMapOf(
//            eDOWN to loadSpriteSheet(context.resources, R.drawable.tokage_down_hit),    // 144 x 207 pixels
//            eUP to loadSpriteSheet(context.resources, R.drawable.tokage_up_hit),        // 144 x 207 pixels
//            eLEFT to loadSpriteSheet(context.resources, R.drawable.tokage_left_hit),    // 207 x 144 pixels
//            eRIGHT to loadSpriteSheet(context.resources, R.drawable.tokage_right_hit)   // 207 x 144 pixels
//        )
//    }

    var filterOpacity by remember{mutableStateOf(0f)}
    var invincibleCycle by remember{mutableStateOf(3)}

    // snapShotMap stored the interaction timestamp in ms
    // If no ID is registered, response is 0L
    val isBeingInteracted = remember(enemy.getID()) {
        derivedStateOf {
            colliderManager.heroInteractedToOther[eColliderType.eCollideEnemy]!![enemy.getID()] ?: 0L     // if no id registered, response as 0L
        }
    }

    fun resetEssentialFlagWhenDie()
    {
        // Stop Move sprite Launch
        startMove.value = false // Stop Move sprite Launch

        // Stop Running Move Thread
        val enemyCharacter = (enemy as EnemyCharacter)
        enemyCharacter.runningMoveThread = false
    }

    // Ever being hit would turn into invincible cycle
    LaunchedEffect(filterOpacity){

        if (invincibleCycle > 0)
        {
            if (filterOpacity > 0f)
            {
                delay(50)
                filterOpacity -= 0.3f
            }
            else if (filterOpacity < 0f)
            {
                filterOpacity = 0.8f
                invincibleCycle--
            }
        }
        else
        {
            filterOpacity = 0.0f
            invincibleCycle = DEFAULT_ENEMY_HURT_INVINCIBLE_CYCLE

            // if not yet die resume the collider
            if (enemy.isDie())
            {
                resetEssentialFlagWhenDie()
                lastDieSpriteFrameIndex = 0 // set start dying
            }
            else
            {
                enemy.getCollider().setActive(true)
            }
        }
    }

    LaunchedEffect(lastDieSpriteFrameIndex)
    {
        // Means started the dying
        if (lastDieSpriteFrameIndex >= spriteDie.size)
        {
            // set
            isEnemyDie = true
            enemy.setDieFinished() // trigger EnemyFactory to remove from pool and collider
        }
        else if (lastDieSpriteFrameIndex >= 0)
        {
            currentSprite.value = spriteDie[lastDieSpriteFrameIndex]

            lastDieSpriteFrameIndex++

            delay(100)
        }
    }

    //
    //    Drag Direction	Example Offset (x, y)	atan2(y, x) in Radians	                Angle in Degrees
    //    Right (→)	        (10, 0)	                atan2(0, 10) = 0	                    0°
    //    Right-Up (↗)	    (10, -10)	            atan2(-10, 10) ≈ -0.785	(-PI / 4)       -45°
    //    Up (↑)	        (0, -10)	            atan2(-10, 0) ≈ -1.571 (-PI / 2)	    -90°
    //    Left-Up (↖)	    (-10, -10)	            atan2(-10, -10) ≈ -2.356 (-3 * PI / 4)	-135°
    //    Left (←)	        (-10, 0)	            atan2(0, -10) = π ≈ 3.142	            180° or -180°
    //    Left-Down (↙)	    (-10, 10)	            atan2(10, -10) ≈ 2.356	(3 * PI / 4)    135°
    //    Down (↓)	        (0, 10)	                atan2(10, 0) ≈ 1.571 (PI / 2)	        90°
    //    Right-Down (↘)	(10, 10)	            atan2(10, 10) ≈ 0.785 (PI / 4)	        45°

    // Move function with angle radian Input
    fun Move(angleRad : Float, repelSpeed : Int = -1)
    {
        // In pixel
        val speed = if (repelSpeed > 0) repelSpeed.toUInt() else enemy.getSpeed()
        val xDeltaPx = speed.toDouble() * cos(angleRad)
        val yDeltaPx = speed.toDouble() * sin(angleRad)

        Log.i("delta", "xDelta ${xDeltaPx},  yDelta ${yDeltaPx}")

        // Temp remark, not consider being blocked by object
//        val collidedObjID = colliderManager.detectMoveCollision(xDeltaPx.toInt(), yDeltaPx.toInt())
//
//        //Log.i("Move", "(${xPos.value}, ${yPos.value}) , xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}   Hit: ${collidedObjID ?: "no hit"}")
//        //Log.i("Check If Allow Move", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}   Hit: ${collidedObjID ?: "no hit"}")
//
//        // Not allow move when hit object
//        if (collidedObjID != null)
//        {
//            return
//        }

        // Normalize angle to the range [0, 2PI)
        val normalizedAngle = ((angleRad + 2 * PI) % (2 * PI)) * (180 / PI)

        Log.i("Enemy Angle","$normalizedAngle ($angleRad), xDelta : $xDeltaPx, yDelta : $yDeltaPx")

        // Update the direction back to the data class
        // if at the repel case, not to update the direction since it is passive movement
        if (repelSpeed < 0)
        {
            when{
                // Left Up to (not include) Right Up -> assign as UP Dir
                normalizedAngle in 225.0..<315.0 -> enemy.setDirection(eDirection.eUP)
                // Right Up to (not include) Right Down -> assign as Right Dir
                normalizedAngle in 315.0 ..<360.0 || normalizedAngle in 0.0..< 45.0 -> enemy.setDirection(eDirection.eRIGHT)
                // Right Down to (not include) Left Down -> assign as Down Dir
                normalizedAngle in 45.0..<135.0 -> enemy.setDirection(eDirection.eDOWN)
                // Left Down to (not include) Left Up -> assign as Left Dir
                normalizedAngle in 135.0..<225.0 -> enemy.setDirection(eDirection.eLEFT)
            }
        }

        //Log.i("Enemy Before Move Pos", "xPos: ${enemy.getXPos()}    yPos: ${enemy.getYPos()}")

        currentMoveJob = CoroutineScope(Dispatchers.Default).launch {

            // X and Y run simultaneously in two individual threads
            val moveXJob = launch{
                enemy.updateXPosByDelta(xDeltaPx.toFloat())
                xPosEnemy = enemy.getXPos()
            }

            val moveYJob = launch{
                enemy.updateYPosByDelta(yDeltaPx.toFloat())
                yPosEnemy = enemy.getYPos()
            }
            moveXJob.join()
            moveYJob.join()
        }
        currentMoveJob?.invokeOnCompletion {
            Log.i("Enemy Final Pos", "xPos: ${enemy.getXPos()}    yPos: ${enemy.getYPos()}")
        }
    }

    // Here Keep Run the Background to move and change the position of enemy
    LaunchedEffect(Unit)
    {
        // Explicit to use separate thread other than main thread
        withContext(Dispatchers.Default){

            val enemyCharacter = (enemy as EnemyCharacter)
            while (enemyCharacter.runningMoveThread)
            {
                // Here keep tracking hero pos and moving to hero angle direction
                val heroXY = getHeroXYPos()

                val xDiff = heroXY.first.toDouble() - enemy.getXPos().toDouble()
                val yDiff = heroXY.second.toDouble() - enemy.getYPos().toDouble()

                var angleInRad = atan2(yDiff,        // delta y
                                        xDiff)        // delta x

//                Log.i("enemy move", ("xDiff: %.2f, yDiff: %.2f, angleInRad: %.2f, hero: (${heroXY.first.toInt()}, ${heroXY.second.toInt()})," +
//                        " enemy pos: (${enemy.getXPos().toInt()}, ${enemy.getYPos().toInt()})")
//                        .format(xDiff, yDiff, angleInRad)
//                )

                Move(angleInRad.toFloat())

                delay(100)
            }
        }
    }

    // ***************************************************** Temp Remark, need change to observe the collider object id that interact by hero
    LaunchedEffect(isBeingInteracted.value)
    {
        // only process when triggered with timestamp recorded
        if (isBeingInteracted.value > 0L)
        {
            enemy.getCollider().setActive(false) // deactive first
            enemy.decrementLives(1U)

            filterOpacity = 0.8f

            val pushBackAngle = when(enemy.getDirection())
            {
                eUP -> PI / 2
                eDOWN -> -PI / 2
                eLEFT -> 0.0
                eRIGHT -> PI
                else -> 0.0
            }
            Move(pushBackAngle.toFloat(), DEFAULT_ENEMY_REPEL_SPEED.toInt())
        }
    }

    fun setCurSpriteWithLastFrameIndex()
    {
        currentSprite.value = spriteMove[enemy.getDirection().value][lastMoveSpriteFrameIndex.value]
    }

    fun AttackingActive(attacking : Boolean)
    {
        isAttacking.value = attacking

        if (attacking)
        {
            lastAttackDir.value = enemy.getDirection()
        }
        enemy.getActionCollider()[lastAttackDir.value]?.setActive(attacking)
    }

    // Moving sprite switching
    LaunchedEffect(startMove.value) {

        var frameIndex = 0
        while(startMove.value)
        {
            setCurSpriteWithLastFrameIndex()

            if (++lastMoveSpriteFrameIndex.value >= spriteMove[enemy.getDirection().value].count())
            {
                lastMoveSpriteFrameIndex.value = 0
            }

            delay(100)
        }
    }

    // ********************** Temp Remark do not have attack sprite for enemy
    // Attack sprite
//    LaunchedEffect(startAttack.value){
//
//        // If it is not the attacking
//        if (startAttack.value &&
//            !isAttacking.value)
//        {
//            startAttack.value = false
//            AttackingActive(true)
//
//            currentSprite.value = spriteAttack[enemy.getDirection()]!!
//        }
//    }

    // When isAttacking changed then trigger
    LaunchedEffect(isAttacking.value)
    {
        // when it is attacking, wait for 50ms as the attack valid time then pull down the flag
        if (isAttacking.value)
        {
            delay(50)
            AttackingActive(false)
        }
        else
        {
            setCurSpriteWithLastFrameIndex()
        }
    }

    // The whole screen can be detect and render the pointer
    Box(Modifier.fillMaxSize()){

        // ********************** Temp Remark do not have attack sprite for enemy
        // Need to change the box size when attacking, since the attack sprite is in rectangle shape
//        val boxSize = with(density)
//        {
//            if (isAttacking.value) {
//
//                when (enemy.getDirection()) {
//                    eDOWN, eUP -> {
//                        CHARACTER_SIZE.toFloat()
//                            .toDp() to CHARACTER_INTERACT_EXTEND_SIZE.toFloat().toDp()
//                    }
//
//                    eLEFT, eRIGHT -> {
//                        CHARACTER_INTERACT_EXTEND_SIZE.toFloat()
//                            .toDp() to CHARACTER_SIZE.toFloat().toDp()
//                    }
//                }
//            } else {
//                CHARACTER_SIZE.toFloat().toDp() to CHARACTER_SIZE.toFloat().toDp()  // Use default size
//            }
//        }

        // ********************** Temp Remark do not have attack sprite for enemy
        // Offset Adjustment for Attacking Up and Left directions
//        val offsetAdjustment = with(density) {
//
//            if (isAttacking.value)
//            {
//                when(enemy.getDirection()){
//                    eUP->{
//                        IntOffset(0, -(CHARACTER_INTERACT_EXTEND_SIZE - CHARACTER_SIZE).toInt())  // Move Up
//                    }
//                    eLEFT->{
//                        IntOffset(-(CHARACTER_INTERACT_EXTEND_SIZE - CHARACTER_SIZE).toInt(), 0)  // Move Left
//                    }
//                    else->{
//                        IntOffset(0, 0)  // No adjustment
//                    }
//                }
//            }
//            else
//            {
//                IntOffset(0, 0)  // No adjustment
//            }
//        }

        val boxSize = with(density)
        {
            CHARACTER_SIZE.toFloat().toDp() to CHARACTER_SIZE.toFloat().toDp()  // Use default size
        }

        val offsetAdjustment = with(density)
        {
            IntOffset(0, 0)  // No adjustment
        }

        // *********************** Temp Remark , enemy do not have action collider
//        if (bFlagDisplayActionCollider)
//        {
//            // Draw the action collider for checking
//            enemy.getActionCollider().forEach{ (eDir, collider)->
//
//                val boxSizeCollider = with(density) {
//                    collider.getSizeWidth().toFloat().toDp() to collider.getSizeHeight().toFloat().toDp()
//                }
//
//                Box(modifier = Modifier
//                    .size(boxSizeCollider.first, boxSizeCollider.second)
//                    .align(Alignment.TopStart)
//                    // Offset is in Pixel
//                    .absoluteOffset {
//                        IntOffset(
//                            collider.getXPos().toInt() - xScreenPos.toInt(),
//                            collider.getYPos().toInt() - yScreenPos.toInt()
//                        )
//                    }
//                    .background(Color.Red)){}
//            }
//        }

        // Character Box (Moves)
        Box(
            modifier = Modifier
                // Assign the Size of Pixel corresponding dp to create the box
                .size(boxSize.first, boxSize.second)
                .align(Alignment.TopStart)
                // Offset is in Pixel
                .absoluteOffset {
                    IntOffset(
                        xPosEnemy.toInt() - xScreenPos.toInt(),
                        yPosEnemy.toInt() - yScreenPos.toInt()
                    ) + offsetAdjustment
                },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = BitmapPainter(currentSprite.value),
                contentDescription = "enemy",
                //contentScale = ContentScale.FillWidth,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(Color.White.copy(alpha = filterOpacity), BlendMode.SrcAtop)
            )
        }
    }
}
