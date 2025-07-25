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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.overrun.R
import com.example.overrun.control.DrawDragDirectionArrow
import com.example.overrun.control.DrawTapCircle
import com.example.overrun.control.GuestureControllerEx
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_HURT_INVINCIBLE_CYCLE
import com.example.overrun.enitities.GameConstant.DEFAULT_HERO_REPEL_SPEED
import com.example.overrun.enitities.GameConstant.ENEMY_CHARACTER_SPRITE_HEIGHT_PIXEL
import com.example.overrun.enitities.GameConstant.ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL
import com.example.overrun.enitities.GameConstant.HERO_CHARACTER_SPRITE_HEIGHT_PIXEL
import com.example.overrun.enitities.GameConstant.HERO_CHARACTER_SPRITE_WIDTH_PIXEL
import com.example.overrun.enitities.GameObjectSizeAndViewManager
import com.example.overrun.enitities.Route.GAME_END
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.eDirection
import com.example.overrun.enitities.eDirection.eDOWN
import com.example.overrun.enitities.eDirection.eLEFT
import com.example.overrun.enitities.eDirection.eRIGHT
import com.example.overrun.enitities.eDirection.eUP
import com.example.overrun.enitities.eHeroType
import com.example.overrun.enitities.eObjectType
import com.example.overrun.enitities.effect.HealthUpEffect
import com.example.overrun.enitities.gameStage.GameMetricsAndControl
import com.example.overrun.enitities.sprites.loadSpriteSheet
import com.example.overrun.enitities.sprites.loadSpriteSheet1D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HeroCompose(hero : HeroCharacter,
                destroyObjFun : (String)->Boolean,
                gameMetricsAndCtrl: GameMetricsAndControl,
                colliderManager: ColliderManager,
                objectSizeAndViewManager : GameObjectSizeAndViewManager)
{

    var isHeroDie by remember { mutableStateOf<Boolean>(hero.isDie()) }
    if (isHeroDie)
    {
        return
    }

    // For Debugging
    val bFlagDisplayActionCollider = false

    // Pass in hero object current stored X and Y Pos
    // Then assign to the xPos and yPos for composable movement animation
    //val xPos = remember { Animatable(hero.getXPos().toFloat()) }
    //val yPos = remember { Animatable(hero.getYPos().toFloat()) }

    // Change to use World Coord for Screen X Y Pos system for trigger rendering
    // Use animateFloat for smooth transition of value
    val xScreenPos by animateFloatAsState(objectSizeAndViewManager.screenWorldX, label="ScreenXPos")
    val yScreenPos by animateFloatAsState(objectSizeAndViewManager.screenWorldY, label="ScreenYPos")

    //Log.i("Screen Pos", "x: ${xScreenPos}    yPos: ${yScreenPos}")

    // An Async handler
    val corontine = rememberCoroutineScope()
    //var isJobFinished = remember{mutableStateOf(true)}
    var currentMoveJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current
    val density = LocalDensity.current

    val CHARACTER_SIZE = objectSizeAndViewManager.GET_CHARACTER_SIZE()
    val CHARACTER_INTERACT_EXTEND_SIZE = objectSizeAndViewManager.GET_CHARACTER_INTERACT_SIZE()

    //Log.i("Density","$density")

    // Load Sprite Once
    // Also store the bitmap resource to prevent recreation during rendering
    val spriteMove = remember{
        loadSpriteSheet(context.resources, (hero as HeroCharacter).getHeroType().resId,
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL, HERO_CHARACTER_SPRITE_HEIGHT_PIXEL,      // 144 x 144 pixels, it related to the .png
            // would do an auto sprite scale up or down from the screen size
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL.toFloat() / objectSizeAndViewManager.GET_CHARACTER_SIZE().toFloat()
        )
    }
    val lastMoveSpriteFrameIndex = remember{ mutableStateOf(0) }

    val spriteDie = remember{
        loadSpriteSheet1D(context.resources, R.drawable.die_sprite,
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL, HERO_CHARACTER_SPRITE_HEIGHT_PIXEL,
            // would do an auto sprite scale up or down from the screen size
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL.toFloat() / objectSizeAndViewManager.GET_CHARACTER_SIZE().toFloat()
        )
    }
    var lastDieSpriteFrameIndex by remember{ mutableStateOf<Int>(-1) } // default not started

    // Below is the fix: conditionally load the correct attacking sprites depending on hero type.
    val spriteAttack = remember {
        val heroType = hero.getHeroType()
        hashMapOf(
            eDOWN to loadSpriteSheet(context.resources, heroType.getAttackSprite(eDOWN)),
            eUP to loadSpriteSheet(context.resources, heroType.getAttackSprite(eUP)),
            eLEFT to loadSpriteSheet(context.resources, heroType.getAttackSprite(eLEFT)),
            eRIGHT to loadSpriteSheet(context.resources, heroType.getAttackSprite(eRIGHT))
        )
    }

    var filterOpacity by remember{ mutableStateOf(0f) }
    var invincibleCycle by remember{ mutableStateOf(3) }

    var healthEffects by remember { mutableStateOf(listOf<Int>()) }
    val mutexHealthEffect = remember { Mutex() }
    val scopeRunHealthEffect = rememberCoroutineScope()

    // snapShot stores the ObjID and interaction timestamp in ms
    val isBeingInteracted = remember {
        derivedStateOf {
            colliderManager.otherInteractedToHero     // default is pair(" ", 0L)
        }
    }

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
            invincibleCycle = DEFAULT_HERO_HURT_INVINCIBLE_CYCLE

            // if not yet die resume the collider
            if (hero.isDie())
            {
                lastDieSpriteFrameIndex = 0 // set start dying
            }
            else
            {
                hero.getCollider().setActive(true)
            }
        }
    }

    // Default Down
    val currentSprite = remember { mutableStateOf(spriteMove[eDirection.eDOWN.value][0]) }

    LaunchedEffect(lastDieSpriteFrameIndex) {
        // Means started the dying
        if (lastDieSpriteFrameIndex >= spriteDie.size)
        {
            // set
            isHeroDie = true
            hero.setDieFinished()
        }
        else if (lastDieSpriteFrameIndex >= 0)
        {
            currentSprite.value = spriteDie[lastDieSpriteFrameIndex]
            lastDieSpriteFrameIndex++
            delay(50)
        }
    }

    //
    //    Drag Direction    Example Offset (x, y)  atan2(y, x) in Radians                   Angle in Degrees
    //    Right (→)         (10, 0)                atan2(0, 10) = 0                          0°
    //    Right-Up (↗)      (10, -10)              atan2(-10, 10) ≈ -0.785 (-PI / 4)         -45°
    //    Up (↑)            (0, -10)               atan2(-10, 0) ≈ -1.571 (-PI / 2)          -90°
    //    Left-Up (↖)       (-10, -10)             atan2(-10, -10) ≈ -2.356 (-3 * PI / 4)    -135°
    //    Left (←)          (-10, 0)               atan2(0, -10) = π ≈ 3.142                180° or -180°
    //    Left-Down (↙)     (-10, 10)              atan2(10, -10) ≈ 2.356 (3 * PI / 4)       135°
    //    Down (↓)          (0, 10)                atan2(10, 0) ≈ 1.571 (PI / 2)             90°
    //    Right-Down (↘)    (10, 10)               atan2(10, 10) ≈ 0.785 (PI / 4)            45°

    // Move function with angle radian input
    fun Move(angleRad : Float, repelSpeed : Int = -1)
    {
        // In pixel
        // Only double the speed if the hero is the orange one (Jacob).
        val baseSpeed = hero.getSpeed()

        // If there's a repel, use that. Otherwise: if orange, 2x speed, else normal base speed.
        val speed = if (repelSpeed > 0) {
            repelSpeed.toUInt()
        } else {
            baseSpeed
        }

        val xDeltaPx = speed.toDouble() * cos(angleRad)
        val yDeltaPx = speed.toDouble() * sin(angleRad)

        val collidedObjID = colliderManager.detectMoveCollision(xDeltaPx.toInt(), yDeltaPx.toInt())

        //Log.i("Move", "(${xPos.value}, ${yPos.value}) , xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}   Hit: ${collidedObjID ?: "no hit"}")
        //Log.i("Check If Allow Move", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}   Hit: ${collidedObjID ?: "no hit"}")

        // Not allow move when hit object
        if (collidedObjID != null)
        {
            return
        }

        // Normalize angle to the range [0, 2PI)
        val normalizedAngle = ((angleRad + 2 * PI) % (2 * PI)) * (180 / PI)

        Log.i("Angle","$normalizedAngle")

        // Update the direction back to the data class
        // if at the repel case, not to update the direction since it is passive movement
        if (repelSpeed < 0)
        {
            when {
                // Left Up to (not include) Right Up -> assign as UP Dir
                normalizedAngle in 225.0..<315.0 -> hero.setDirection(eDirection.eUP)
                // Right Up to (not include) Right Down -> assign as Right Dir
                normalizedAngle in 315.0..<360.0 || normalizedAngle in 0.0..<45.0 -> hero.setDirection(eDirection.eRIGHT)
                // Right Down to (not include) Left Down -> assign as Down Dir
                normalizedAngle in 45.0..<135.0 -> hero.setDirection(eDirection.eDOWN)
                // Left Down to (not include) Left Up -> assign as Left Dir
                normalizedAngle in 135.0..<225.0 -> hero.setDirection(eDirection.eLEFT)
            }
        }

        // Cancel the previous job
        //currentMoveJob?.cancel()

        //Log.i("Before Move Pos", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")

        currentMoveJob = CoroutineScope(Dispatchers.Default).launch {
            // X and Y run simultaneously in two individual threads
            val moveXJob = launch {
                //xPos.animateTo(xPos.value + xDeltaPx.toFloat())

                // Also move the screen X when hero moves
                objectSizeAndViewManager.screenWorldX += xDeltaPx.toFloat()

                // Update Hero X Pos at the screen center
                val newXPos = objectSizeAndViewManager.screenWorldX +
                        (objectSizeAndViewManager.getScreenWidth().toFloat() / 2.0f) -
                        (CHARACTER_SIZE.toFloat() / 2.0f)
                hero.updateXPos(newXPos.toUInt())
            }
            val moveYJob = launch {
                //yPos.animateTo(yPos.value + yDeltaPx.toFloat())

                // Also move the screen Y when hero moves
                objectSizeAndViewManager.screenWorldY += yDeltaPx.toFloat()

                // Update Hero Y Pos at the screen center
                val newYPos = objectSizeAndViewManager.screenWorldY +
                        (objectSizeAndViewManager.getScreenHeight().toFloat() / 2.0f) -
                        (CHARACTER_SIZE.toFloat() / 2.0f)
                hero.updateYPos(newYPos.toUInt())
            }
            moveXJob.join()
            moveYJob.join()
        }
        currentMoveJob?.invokeOnCompletion {
            //hero.updatePosition(xPos.value.toUInt(), yPos.value.toUInt())
            //Log.i("Final Pos", "(${xPos.value}, ${yPos.value}) , xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")
            Log.i("Final Pos", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")
        }
    }

    fun AddHealth(health: UInt)
    {
        hero.incrementLives(health)

        scopeRunHealthEffect.launch {
            mutexHealthEffect.withLock {
                healthEffects = healthEffects + listOf(health.toInt())
            }
        }
    }

    LaunchedEffect(isBeingInteracted.value) {
        // only process when triggered with timestamp recorded
        if (isBeingInteracted.value.second > 0L) {

            // Can apply for Object to debug for interaction
            val objectID = isBeingInteracted.value.first
            val interactObj = eObjectType.fromIDStringToObjType(objectID)

            // under different object configuration to set the object data
            // TO DO
            // Set Destroy, Active or InActive
            if (interactObj != null)
            {
                if (interactObj.isHealthUpGem())
                {
                    if (destroyObjFun(objectID))
                    {
                        val healthSize = if (interactObj == eObjectType.ePOWER_HEALTH_GEM) 5U else 1U
                        AddHealth(healthSize)
                    }
                }
                else if (interactObj.isHarmful()) // if the object is harmful
                {
                    // Added here to decrement hero life by 1 unit
                    hero.decrementLives(1U)
                    hero.getCollider().setActive(false) // deactivate first
                    filterOpacity = 0.8f

                    val pushBackAngle = when(hero.getDirection())
                    {
                        eUP -> PI / 2
                        eDOWN -> -PI / 2
                        eLEFT -> 0.0
                        eRIGHT -> PI
                        else -> 0.0
                    }
                    Move(pushBackAngle.toFloat(), DEFAULT_HERO_REPEL_SPEED.toInt())
                }
            }
        }
    }

    // Control and Render
    val pointerAlpha = remember { Animatable(0f) }
    val pointerAngle = remember { mutableFloatStateOf(0f) }
    val startMove = remember { mutableStateOf(false) }

    val startAttack = remember { mutableStateOf(false) }
    val mutexStartAttack = remember { Mutex() }
    val scopeStartAttack = rememberCoroutineScope()
    val isAttacking = remember { mutableStateOf(false) }
    val lastAttackDir = remember { mutableStateOf(hero.getDirection()) }

    // Offset includes x and y
    val touchStartPt = remember { mutableStateOf(Offset.Zero) }
    val touchAlpha = remember { Animatable(0f) }

    fun setCurSpriteWithLastFrameIndex()
    {
        currentSprite.value = spriteMove[hero.getDirection().value][lastMoveSpriteFrameIndex.value]
    }

    fun StartAttack(start : Boolean)
    {
        scopeStartAttack.launch {
            mutexStartAttack.withLock {
                startAttack.value = start
            }
        }
    }

    fun AttackingActive(attacking : Boolean)
    {
        isAttacking.value = attacking
        if (attacking)
        {
            lastAttackDir.value = hero.getDirection()
        }
        else
        {
            StartAttack(false)
        }
        hero.getActionCollider()[lastAttackDir.value]?.setActive(attacking)
    }

    // Moving sprite switching
    LaunchedEffect(startMove.value) {
        var frameIndex = 0
        while(startMove.value)
        {
            if (!gameMetricsAndCtrl.isGamePaused.value)
            {
                setCurSpriteWithLastFrameIndex()
                if (++lastMoveSpriteFrameIndex.value >= spriteMove[hero.getDirection().value].count())
                {
                    lastMoveSpriteFrameIndex.value = 0
                }
            }
            delay(100)
        }
    }

    // Attack sprite
    LaunchedEffect(startAttack.value) {
        // If it is not currently attacking
        if (startAttack.value && !isAttacking.value)
        {
            mutexStartAttack.withLock {
                Log.i("Start Attack", "Start Attack : ${startAttack.value}, IsAttacking : ${isAttacking.value}")
                StartAttack(false)
                AttackingActive(true)

                currentSprite.value = spriteAttack[hero.getDirection()]!!
            }
        }
    }

    // When isAttacking changed then trigger
    LaunchedEffect(isAttacking.value) {
        // when it is attacking, wait for 50ms as the attack valid time then pull down the flag
        if (isAttacking.value)
        {
            delay(50)
            AttackingActive(false)
            Log.i("End Attack", "Start Attack : ${startAttack.value}, IsAttacking : ${isAttacking.value}")
        }
        else
        {
            setCurSpriteWithLastFrameIndex()
        }
    }

    // The whole screen can detect and render the pointer
    Box(Modifier.fillMaxSize()) {

        // Drag detect
        GuestureControllerEx(
            onTap = { touchPt ->
                if (!gameMetricsAndCtrl.isGamePaused.value)
                {
                    touchStartPt.value = touchPt // Store the Touch Pos
                    Log.i("Tap", "Touch pt $touchStartPt.value, Start Attack : ${startAttack.value}, IsAttacking : ${isAttacking.value}")
                    StartAttack(true)
                    corontine.launch(Dispatchers.Default) {
                        touchAlpha.snapTo(0.4f)
                    }
                }
            },
            onTapEnd = { isDragStarted ->
                if (!gameMetricsAndCtrl.isGamePaused.value) {
                    corontine.launch(Dispatchers.Default) {
                        if (!isDragStarted) {
                            touchAlpha.snapTo(0f)
                            setCurSpriteWithLastFrameIndex()
                        } else {
                            touchAlpha.animateTo(0f, tween(150))
                        }
                    }
                }
            },
            onDrag = { angle ->
                if (!gameMetricsAndCtrl.isGamePaused.value) {
                    pointerAngle.floatValue = angle
                    AttackingActive(false) // cancel the attack if we move
                    // Call the Move to change xPos and yPos under the angle
                    Move(angle)
                }
            },
            // When Drag starts
            onDragStart = { dragStartPt ->
                if (!gameMetricsAndCtrl.isGamePaused.value) {
                    startMove.value = true
                    corontine.launch(Dispatchers.Default) {
                        // Set the Alpha value to 1f, for pointer arrow rendering
                        pointerAlpha.snapTo(0.4f)
                    }
                }
            },
            // When Drag ends
            onDragEnd = {
                if (!gameMetricsAndCtrl.isGamePaused.value) {
                    startMove.value = false
                    corontine.launch(Dispatchers.Default) {
                        pointerAlpha.animateTo(0f, tween(150)) // use 250 ms change from 1 to 0
                    }
                }
            }
        )

        // Draw tap circle response
        DrawTapCircle(touchStartPt.value, touchAlpha.value)

        // Draw the arrow for the drag action
        DrawDragDirectionArrow(touchStartPt.value, pointerAlpha.value, pointerAngle.floatValue)

        // Need to change the box size when attacking, since the attack sprite is in a rectangle shape
        val boxSize = with(density) {
            if (isAttacking.value) {
                when (hero.getDirection()) {
                    eDOWN, eUP -> {
                        CHARACTER_SIZE.toFloat().toDp() to CHARACTER_INTERACT_EXTEND_SIZE.toFloat().toDp()
                    }
                    eLEFT, eRIGHT -> {
                        CHARACTER_INTERACT_EXTEND_SIZE.toFloat().toDp() to CHARACTER_SIZE.toFloat().toDp()
                    }
                }
            } else {
                CHARACTER_SIZE.toFloat().toDp() to CHARACTER_SIZE.toFloat().toDp()  // Use default size
            }
        }

        // Offset adjustment for Attacking Up and Left directions
        val offsetAdjustment = with(density) {
            if (isAttacking.value)
            {
                when(hero.getDirection()){
                    eUP->{
                        IntOffset(0, -(CHARACTER_INTERACT_EXTEND_SIZE - CHARACTER_SIZE).toInt())  // Move Up
                    }
                    eLEFT->{
                        IntOffset(-(CHARACTER_INTERACT_EXTEND_SIZE - CHARACTER_SIZE).toInt(), 0)  // Move Left
                    }
                    else->{
                        IntOffset(0, 0)  // No adjustment
                    }
                }
            }
            else
            {
                IntOffset(0, 0)  // No adjustment
            }
        }

        if (bFlagDisplayActionCollider)
        {
            // Draw the action colliders for checking
            hero.getActionCollider().forEach { (eDir, collider) ->
                val boxSizeCollider = with(density) {
                    collider.getSizeWidth().toFloat().toDp() to collider.getSizeHeight().toFloat().toDp()
                }
                Box(
                    modifier = Modifier
                        .size(boxSizeCollider.first, boxSizeCollider.second)
                        .align(Alignment.TopStart)
                        // Offset is in pixels
                        .absoluteOffset {
                            IntOffset(
                                collider.getXPos().toInt() - xScreenPos.toInt(),
                                collider.getYPos().toInt() - yScreenPos.toInt()
                            )
                        }
                        .background(Color.Red)
                ) {}
            }
        }

        // Character Box (moves)
        Box(
            modifier = Modifier
                // Assign the size of pixel-corresponding dp to create the box
                .size(boxSize.first, boxSize.second)
                .align(Alignment.TopStart)
                // Offset is in pixels
                .absoluteOffset {
                    IntOffset(
                        hero.getXPos().toInt() - xScreenPos.toInt(),
                        hero.getYPos().toInt() - yScreenPos.toInt()
                    ) + offsetAdjustment
                },
            contentAlignment = Alignment.Center
        ) {
            //  Text("Hero(${xPos.value}, ${yPos.value})", color = Color.White, fontSize = 16.sp,
            //      textAlign = TextAlign.Center)

            Image(
                painter = BitmapPainter(currentSprite.value),
                contentDescription = "hero",
                //contentScale = ContentScale.FillWidth,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(Color.Red.copy(alpha = filterOpacity), BlendMode.SrcAtop)
            )
        }

        // Display the +1HP (or +5HP, etc.) effect
        healthEffects.forEachIndexed { index, amount ->
            HealthUpEffect(
                amount = amount,
                modifier = Modifier
                    .size(boxSize.first, boxSize.second)
                    .align(Alignment.Center)
                    .absoluteOffset {
                        IntOffset(
                            hero.getXPos().toInt() - xScreenPos.toInt(),
                            hero.getYPos().toInt() - yScreenPos.toInt()
                        ) + IntOffset(0, (-10).toInt())
                    },
                onEffectComplete = {
                    // Safely remove the health effect when it completes
                    scopeRunHealthEffect.launch {
                        try {
                            mutexHealthEffect.withLock {
                                if (index in healthEffects.indices) {
                                    healthEffects = healthEffects.toMutableList().apply {
                                        removeAt(index)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Handle the error here, outside the composable function
                            Log.e("HealthEffectError", "Error removing health effect: $e")
                        }
                    }
                }
            )
        }
    }
}
