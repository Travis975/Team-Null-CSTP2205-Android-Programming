package com.example.gohero.enitities.character

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gohero.control.DragController
import com.example.gohero.control.DrawDragDirectionArrow
import com.example.gohero.control.DrawTapCircle
import com.example.gohero.control.GuestureControllerEx
import com.example.gohero.enitities.GameConstant.HERO_PIXEL_SIZE
import com.example.gohero.enitities.eDirection
import com.example.overrun.R
import com.example.overrun.enitities.sprites.loadSpriteSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@Composable
fun HeroCompose(hero : CharacterBase){

    // Pass in hero object current stored X and Y Pos
    // Then assign to the xPos and yPos for composable movement animation
    val xPos = remember { Animatable(hero.getXPos().toFloat()) }
    val yPos = remember { Animatable(hero.getYPos().toFloat()) }

    // An Async handler
    val corontine = rememberCoroutineScope()
    //var isJobFinished = remember{mutableStateOf(true)}
    var currentMoveJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current

    // Load Sprite Once
    val spriteMove = remember{
        loadSpriteSheet(context.resources, (hero as HeroCharacter).getHeroType().resId,
            HERO_PIXEL_SIZE, HERO_PIXEL_SIZE // 96 x 96 pixels ^ 2, a multiple of 16
        )
    }

    // Default Down
    var currentMoveSprite = remember{ mutableStateOf(spriteMove[eDirection.eDOWN.value][0]) }

    fun Move(eDir : eDirection){

        var speed = hero.getSpeed().toFloat()
        var targetPos: Float

        var dir = 1
        if (eDir == eDirection.eUP || eDir == eDirection.eLEFT)
        {
            dir *= -1
        }

        currentMoveJob = corontine.launch{

            hero.setDirection(eDir)

            when(eDir) {
                // move up is to decrease the y position
                eDirection.eUP, eDirection.eDOWN -> {
                    targetPos = yPos.value + (dir * speed)
                    yPos.animateTo(targetPos)
                }

                eDirection.eLEFT, eDirection.eRIGHT -> {
                    targetPos = xPos.value + (dir * speed)
                    xPos.animateTo(targetPos)
                }
            }
        }

        currentMoveJob?.invokeOnCompletion {
            hero.updatePosition(xPos.value.toUInt(), yPos.value.toUInt())
            Log.i("Final Pos", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")
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

    // Overload Move function with angle radian Input then move with the speed magnitude
    fun Move(angleRad : Float)
    {
        val xDelta = hero.getSpeed().toDouble() * cos(angleRad)
        val yDelta = hero.getSpeed().toDouble() * sin(angleRad)

        Log.i("Move", "xDelta: ${xDelta}    yDelta: ${yDelta}")

        // Normalize angle to the range [0, 2PI)
        val normalizedAngle = ((angleRad + 2 * PI) % (2 * PI)) * (180 / PI)

        Log.i("Angle","${normalizedAngle}")

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
                xPos.animateTo(xPos.value + xDelta.toFloat())
            }
            val moveYJob = launch{
                yPos.animateTo(yPos.value + yDelta.toFloat())
            }
            moveXJob.join()
            moveYJob.join()
        }
        currentMoveJob?.invokeOnCompletion {
            hero.updatePosition(xPos.value.toUInt(), yPos.value.toUInt())
            Log.i("Final Pos", "xPos: ${hero.getXPos()}    yPos: ${hero.getYPos()}")
        }
    }

    // Control and Render
    var pointerAlpha = remember {Animatable(0f)}
    var pointerAngle = remember {mutableStateOf(0f)}
    var startMove = remember{mutableStateOf(false)}

    // Offset include x and y
    val touchStartPt = remember {mutableStateOf(Offset.Zero)}
    var touchAlpha = remember{Animatable(0f)}



    // when start moving
    LaunchedEffect(startMove.value) {

        if (startMove.value){

            var frameIndex = 0
            while(true)
            {
                if (frameIndex >= spriteMove[hero.getDirection().value].count())
                {
                    frameIndex = 0
                }

                currentMoveSprite.value = spriteMove[hero.getDirection().value][frameIndex]
                frameIndex++;
                delay(100)
            }
        }

    }

    // The whole screen can be detect and render the pointer
    Box(Modifier.fillMaxSize()){

        // Drag detect
        GuestureControllerEx(

            onTap = { touchPt ->
                touchStartPt.value = touchPt // Store the Touch Pos

                corontine.launch{
                    touchAlpha.snapTo(0.4f)
                }
            },

            onTapEnd = { isDragStarted ->
                corontine.launch {
                    if (!isDragStarted)
                    {
                        touchAlpha.snapTo(0f)
                    }
                    else
                    {
                        touchAlpha.animateTo(0f, tween(150))
                    }
                }
            },

            onDrag = { angle ->
                pointerAngle.value = angle

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
                                pointerAlpha.value, pointerAngle.value)

        // Character Box (Moves)
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset { IntOffset(xPos.value.toInt(), yPos.value.toInt()) },
                //.background(Color.Green, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
//            Text("Hero(${xPos.value}, ${yPos.value})", color = Color.White, fontSize = 16.sp,
//                textAlign = TextAlign.Center)

            Image(
                painter = BitmapPainter(currentMoveSprite.value),
                contentDescription = "hero",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}
