package com.example.gohero.enitities.character

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.gohero.control.DrawDragDirectionArrow
import com.example.gohero.control.DrawTapCircle
import com.example.gohero.control.GuestureControllerEx
import com.example.gohero.enitities.GameConstant.HERO_CHARACTER_SPRITE_HEIGHT_PIXEL
import com.example.gohero.enitities.GameConstant.HERO_CHARACTER_SPRITE_WIDTH_PIXEL
import com.example.gohero.enitities.eDirection
import com.example.gohero.enitities.eDirection.eDOWN
import com.example.gohero.enitities.eDirection.eLEFT
import com.example.gohero.enitities.eDirection.eRIGHT
import com.example.gohero.enitities.eDirection.eUP
import com.example.overrun.R
import com.example.overrun.enitities.GameObjectSizeManager
import com.example.overrun.enitities.collider.ColliderManager
import com.example.overrun.enitities.sprites.loadSpriteSheet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HeroCompose(hero : CharacterBase,
                colliderManager: ColliderManager,
                objectSizeManager : GameObjectSizeManager) {

    // Pass in hero object current stored X and Y Pos
    // Then assign to the xPos and yPos for composable movement animation
    val xPos = remember { Animatable(hero.getXPos().toFloat()) }
    val yPos = remember { Animatable(hero.getYPos().toFloat()) }

    // An Async handler
    val corontine = rememberCoroutineScope()
    //var isJobFinished = remember{mutableStateOf(true)}
    var currentMoveJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current
    val density = LocalDensity.current

    val CHARACTER_SIZE = objectSizeManager.GET_CHARACTER_SIZE()
    val CHARACTER_INTERACT_EXTEND_SIZE = objectSizeManager.GET_CHARACTER_INTERACT_SIZE()

    //Log.i("Density","$density")

    // Load Sprite Once
    val spriteMove = remember{
        loadSpriteSheet(context.resources, (hero as HeroCharacter).getHeroType().resId,
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL, HERO_CHARACTER_SPRITE_HEIGHT_PIXEL,      // 144 x 144 pixels, it related to the .png
            // would do a auto sprite scale up or down from the screen size
            HERO_CHARACTER_SPRITE_WIDTH_PIXEL.toFloat() / objectSizeManager.GET_CHARACTER_SIZE().toFloat()
        )
    }
    val lastMoveSpriteFrameIndex = remember{ mutableStateOf(0) }

    val spriteAttack = remember{
        hashMapOf(
            eDOWN to loadSpriteSheet(context.resources, R.drawable.tokage_down_hit),    // 144 x 207 pixels
            eUP to loadSpriteSheet(context.resources, R.drawable.tokage_up_hit),        // 144 x 207 pixels
            eLEFT to loadSpriteSheet(context.resources, R.drawable.tokage_left_hit),    // 207 x 144 pixels
            eRIGHT to loadSpriteSheet(context.resources, R.drawable.tokage_right_hit)   // 207 x 144 pixels
        )
    }
//

    // Default Down
    val currentSprite = remember{ mutableStateOf(spriteMove[eDirection.eDOWN.value][0]) }

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
    fun Move(angleRad : Float)
    {
        // In pixel
        val xDeltaPx = hero.getSpeed().toDouble() * cos(angleRad)
        val yDeltaPx = hero.getSpeed().toDouble() * sin(angleRad)

        val collidedObjID = colliderManager.detectMoveCollision(xDeltaPx.toInt(), yDeltaPx.toInt())

        Log.i("Move", "(${xPos.value}, ${yPos.value}) , xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}   Hit: ${collidedObjID ?: "no hit"}")

        // Not allow move when hit object
        if (collidedObjID != null)
        {
            return
        }

        // Normalize angle to the range [0, 2PI)
        val normalizedAngle = ((angleRad + 2 * PI) % (2 * PI)) * (180 / PI)

        Log.i("Angle","$normalizedAngle")

        // Update the direction back to the data class
        when{
            // Left Up to (not include) Right Up -> assign as UP Dir
            normalizedAngle in 225.0..<315.0 -> hero.setDirection(eDirection.eUP)
            // Right Up to (not include) Right Down -> assign as Right Dir
            normalizedAngle in 315.0 ..<360.0 || normalizedAngle in 0.0..< 45.0 -> hero.setDirection(eDirection.eRIGHT)
            // Right Down to (not include) Left Down -> assign as Down Dir
            normalizedAngle in 45.0..<135.0 -> hero.setDirection(eDirection.eDOWN)
            // Left Down to (not include) Left Up -> assign as Left Dir
            normalizedAngle in 135.0..<225.0 -> hero.setDirection(eDirection.eLEFT)
        }

        // Cancel the previous job
        //currentMoveJob?.cancel()

        Log.i("Pos", "xPos: ${xPos.value}    yPos: ${yPos.value}")

        currentMoveJob = corontine.launch {

            // X and Y run simultaneously in two individual threads
            val moveXJob = launch{
                xPos.animateTo(xPos.value + xDeltaPx.toFloat())
            }
            val moveYJob = launch{
                yPos.animateTo(yPos.value + yDeltaPx.toFloat())
            }
            moveXJob.join()
            moveYJob.join()
        }
        currentMoveJob?.invokeOnCompletion {
            hero.updatePosition(xPos.value.toUInt(), yPos.value.toUInt())
            Log.i("Final Pos", "(${xPos.value}, ${yPos.value}) , xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")
        }
    }

    // Control and Render
    val pointerAlpha = remember {Animatable(0f)}
    val pointerAngle = remember {mutableFloatStateOf(0f)}
    val startMove = remember{mutableStateOf(false)}

    val startAttack = remember{mutableStateOf(false)}
    val isAttacking = remember{mutableStateOf(false)}

    // Offset include x and y
    val touchStartPt = remember {mutableStateOf(Offset.Zero)}
    val touchAlpha = remember{Animatable(0f)}

    fun setCurSpriteWithLastFrameIndex()
    {
        currentSprite.value = spriteMove[hero.getDirection().value][lastMoveSpriteFrameIndex.value]
    }

    // Moving sprite switching
    LaunchedEffect(startMove.value) {

        var frameIndex = 0
        while(startMove.value)
        {
            setCurSpriteWithLastFrameIndex()

            if (++lastMoveSpriteFrameIndex.value >= spriteMove[hero.getDirection().value].count())
            {
                lastMoveSpriteFrameIndex.value = 0
            }

            delay(100)
        }
    }

    // Attack sprite
    LaunchedEffect(startAttack.value){

        // If it is not the attacking
        if (startAttack.value &&
            !isAttacking.value)
        {
            startAttack.value = false
            isAttacking.value = true

            currentSprite.value = spriteAttack[hero.getDirection()]!!
        }
    }

    // When isAttacking changed then trigger
    LaunchedEffect(isAttacking.value)
    {
        // when it is attacking, wait for 50ms as the attack valid time then pull down the flag
        if (isAttacking.value)
        {
            delay(50)
            isAttacking.value = false
        }
        else
        {
            setCurSpriteWithLastFrameIndex()
        }
    }

    // The whole screen can be detect and render the pointer
    Box(Modifier.fillMaxSize()){

        // Drag detect
        GuestureControllerEx(

            onTap = { touchPt ->
                touchStartPt.value = touchPt // Store the Touch Pos

                startAttack.value = true

                corontine.launch{
                    touchAlpha.snapTo(0.4f)
                }
            },

            onTapEnd = { isDragStarted ->
                corontine.launch {
                    if (!isDragStarted)
                    {
                        touchAlpha.snapTo(0f)
                        isAttacking.value = false
                        setCurSpriteWithLastFrameIndex()
                    }
                    else
                    {
                        touchAlpha.animateTo(0f, tween(150))
                    }
                }
            },

            onDrag = { angle ->
                pointerAngle.floatValue = angle

                isAttacking.value = false // cancel the attack if is moving

                // Call the Move to change xPos and yPos under the angle
                Move(angle)
            },

            // When Drag started
            onDragStart = { dragStartPt ->
                startMove.value = true
                corontine.launch{
                    // Set the Alpha value to 1f, for pointer arrow rendering
                    pointerAlpha.snapTo(0.4f)
                }
            },

            // When Drag End
            onDragEnd = {
                startMove.value = false
                corontine.launch{
                    pointerAlpha.animateTo(0f, tween(150)) // use 250 ms change from 1 to 0
                }
            }
        )

        // Draw Tap circle response
        DrawTapCircle(touchStartPt.value, touchAlpha.value)

        // Draw the Arrow for the Drag Action
        DrawDragDirectionArrow(touchStartPt.value,
                                pointerAlpha.value, pointerAngle.floatValue)


        // Need to change the box size when attacking, since the attack sprite is in rectangle shape
        val boxSize = with(density)
        {
            if (isAttacking.value) {

                when (hero.getDirection()) {
                    eDOWN, eUP -> {
                        CHARACTER_SIZE.toFloat()
                            .toDp() to CHARACTER_INTERACT_EXTEND_SIZE.toFloat().toDp()
                    }

                    eLEFT, eRIGHT -> {
                        CHARACTER_INTERACT_EXTEND_SIZE.toFloat()
                            .toDp() to CHARACTER_SIZE.toFloat().toDp()
                    }
                }
            } else {
                CHARACTER_SIZE.toFloat().toDp() to CHARACTER_SIZE.toFloat().toDp()  // Use default size
            }
        }

        // Offset Adjustment for Attacking Up and Left directions
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

        // Character Box (Moves)
        Box(
            modifier = Modifier
                // Assign the Size of Pixel corresponding dp to create the box
                .size(boxSize.first, boxSize.second)
                //.size(boxSize)
                .align(Alignment.TopStart)
                // Offset is in Pixel
                .absoluteOffset { IntOffset(xPos.value.toInt(), yPos.value.toInt()) + offsetAdjustment },
                //.background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
//            Text("Hero(${xPos.value}, ${yPos.value})", color = Color.White, fontSize = 16.sp,
//                textAlign = TextAlign.Center)

            Image(
                painter = BitmapPainter(currentSprite.value),
                contentDescription = "hero",
                //contentScale = ContentScale.FillWidth,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}
