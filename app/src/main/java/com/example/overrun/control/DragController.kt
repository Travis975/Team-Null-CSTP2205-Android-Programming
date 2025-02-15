package com.example.gohero.control

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// Create a Controller such that implement the
// onDrag, onDragStart, and onDragEnd inside the Implementing Composable

// The onDrag fail to fire after stopped the movement
// On step trigger
@Composable
fun DragController(
    onDrag: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            // Design to be detect the whole screen
            .fillMaxSize()
            .pointerInput(Unit) {
                //detectDragGesturesAfterLongPress(
                detectDragGestures(
                    onDragStart = { touchPt -> onDragStart(touchPt) },
                    onDragEnd = { onDragEnd() }
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
    )
}

// Allow to keep onDrag to fire until no pressing the screen
@Composable
fun GuestureControllerEx(
    onTap: (Offset) ->Unit,
    onTapEnd: (Boolean)->Unit,
    onDrag: (Float) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var isTouching = remember{ mutableStateOf(false)}
    var isDragStart = remember { mutableStateOf(false)}
    var persistAngle = remember {mutableStateOf(0f)}
    val coroutineScope = rememberCoroutineScope()

    val movememntThreshold = 5f

    Box(
        modifier = Modifier
            // Design to be detect the whole screen
            .fillMaxSize()
            .pointerInput(Unit) {
                // Wait for the first touch to start a gesture run
                awaitEachGesture {
                    // get for first touch
                    val down = awaitFirstDown()
                    isTouching.value = true
                    onTap(down.position)

                    // Start a coroutine to keep calling onDrag() while touching
                    val dragActionThread = coroutineScope.launch {
                        while (isTouching.value){

                            Log.i("GestureController", "Persist Angle: ${persistAngle.value}")

                            if(isDragStart.value) {
                                onDrag(persistAngle.value)
                            }
                            delay(10)
                        }
                    }

                    try {
                        while (isTouching.value)
                        {
                            // This will hold for any on top gesture event trigger
                            val event = awaitPointerEvent()
                            val pan = event.changes.firstOrNull()?.positionChange() ?: Offset.Zero

                            if (pan != Offset.Zero &&
                                pan.getDistance() >= movememntThreshold) {

                                if (!isDragStart.value){
                                    isDragStart.value = true

                                    onDragStart(event.changes.first().position)
                                }

                                persistAngle.value = atan2(pan.y, pan.x)
                                Log.i("GestureController", "Update Angle: ${persistAngle.value}")
                                println("y : ${pan.y}, x : ${pan.x}")
                            }

                            // Check if the pointer is lifted
                            if (event.changes.none { it.pressed }) {
                                break
                            }
                        }
                    } finally {
                        isTouching.value = false
                        dragActionThread.cancel()

                        if (isDragStart.value)
                        {
                            onDragEnd()
                        }

                        onTapEnd(isDragStart.value)
                        isDragStart.value = false
                        persistAngle.value = 0f
                        Log.i("GestureController", "Released")
                    }
                }
            }
    )
}
@Composable
fun DrawTapCircle(touchStartPt: Offset, tapAlpha : Float){
    Canvas(modifier = Modifier.fillMaxSize(),
        onDraw = {

            val radius = 100f
            val center = touchStartPt

            // When Tap Started
            if (tapAlpha > 0f){
                val gradientBrush = Brush.radialGradient(
                    colors = listOf(Color.White, Color.Gray,
                                Color.DarkGray, Color.Transparent), // From Gray to transparent
                    center = center,
                    radius = radius
                )

                val path = Path().apply {
                    addOval(Rect(center - Offset(radius, radius), center + Offset(radius, radius)))
                }

                drawPath(
                    path = path,
                    brush = gradientBrush,
                    style = Fill
                )
            }
        })
}

@Composable
fun DrawDragDirectionArrow(touchStartPt : Offset,
                           pointerAlpha : Float, pointerAngle : Float)
{
    // Draw the Arrow for the Drag Action
    Canvas(modifier = Modifier.fillMaxSize(),
    onDraw = {
        // When On Drag Start
        if (pointerAlpha > 0f) {

            val pointerSize = 200f
            val pointerBaseWidth = 40f
            val center = touchStartPt

            val perpendLeftAngle = (pointerAngle + (PI / 2)).toFloat()
            val perpendRightAngle = (pointerAngle - (PI / 2)).toFloat()

            val tip = center + Offset(
                pointerSize * cos(pointerAngle),
                pointerSize * sin(pointerAngle)
            )
            val left = center + Offset(
                pointerBaseWidth * cos(perpendLeftAngle),
                pointerBaseWidth * sin(perpendLeftAngle)
            )
            val right = center + Offset(
                pointerBaseWidth * cos(perpendRightAngle),
                pointerBaseWidth * sin(perpendRightAngle)
            )

            // Triangle shape
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(left.x, left.y)
                lineTo(right.x, right.y)
                close()
            }

            val gradientBrush = Brush.radialGradient(
                colors = listOf(
                    Color.Gray.copy(alpha = 0.6f), // Solid in center
                    Color.Gray.copy(alpha = 0.4f), // Solid in center
                    Color.Gray.copy(alpha = 0.2f), // Fades at edges
                    Color.Transparent // Fully transparent at the outer edge
                ),
                center = touchStartPt,
                radius = 250f
            )

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.2f),
                //brush = gradientBrush,
                style = Fill
            )

            drawPath(
                path = path,
                //color = Color.Gray.copy(alpha = pointerAlpha.value),
                brush = gradientBrush,
                style = Stroke(
                    width = 80f, // Set the stroke width
                    join = StrokeJoin.Round, // Rounded corner joins
                    cap = StrokeCap.Round // Rounded end caps
                )
            )
        }
    })
}

//@Composable
//fun GuestureControllerEx(
//    onDrag: (Float) -> Unit,
//    onDragStart: (Offset) -> Unit,
//    onDragEnd: () -> Unit
//) {
//    var isTouching = remember{ mutableStateOf(false)}
//    var isDragStart = remember { mutableStateOf(false)}
//    var presistAngle = remember {mutableStateOf(0f)}
//
//    Box(
//        modifier = Modifier
//            // Design to be detect the whole screen
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectTransformGestures { centroid, pan, zoom, rotate ->
//                    // When keep touching
//                    if (isTouching.value)
//                    {
//                        if (!isDragStart.value)
//                        {
//                            isDragStart.value = true
//                            onDragStart(centroid)
//                        }
//
//                        // If the Movement stop, use presistAngle to keep moving, otherwise update to the latest angle
//                        val angle : Float = if (pan != Offset.Zero) atan2(pan.y, pan.x) else presistAngle.value
//                        presistAngle.value = angle
//
//                        onDrag(angle)
//                    }
//                }
//            }
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        try{
//                            isTouching.value = true
//                            awaitRelease()  // hold
//                        }
//                        finally{
//                            // release
//                            isTouching.value = false
//                            isDragStart.value = false
//                            onDragEnd()
//                        }
//                    },
//                    onTap = {
//                        // TO DO for some character action
//                    },
//                    onDoubleTap = {
//                        // TO DO for some character action
//                    },
//                    onLongPress = {
//                        // TO DO for some character action
//                    }
//                )
//            }
//    )
//}
