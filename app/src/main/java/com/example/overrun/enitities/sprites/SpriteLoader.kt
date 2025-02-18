package com.example.overrun.enitities.sprites

import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

private fun createFrame(frameWidth: Int, frameHeight: Int, x: Int, y: Int, imageBitmap: ImageBitmap): ImageBitmap
{
    // Create an ImageBitmap to hold the frame
    val frame = ImageBitmap(frameWidth, frameHeight)

    // Create a Canvas to draw the image onto the frame
    val canvas = Canvas(frame)

    // Draw the image on the canvas
    canvas.drawImageRect(
        image = imageBitmap,
        srcOffset = IntOffset(x * frameWidth, y * frameHeight),
        srcSize = IntSize(frameWidth, frameHeight),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(frameWidth, frameHeight),
        paint = Paint() // pass in a default paint obj
    )

    return frame
}

// Each Row is one kind of animation, the column of each row is the frame alternation
// Return a 2D List of ImageBitmap
fun loadSpriteSheet(res: android.content.res.Resources, @DrawableRes resId: Int,
                    frameWidth: Int, frameHeight: Int): MutableList<MutableList<ImageBitmap>> {

    val options = BitmapFactory.Options().apply {
        inScaled = false  // Disable scaling based on density
    }

    val imageBitmap = BitmapFactory.decodeResource(res, resId, options).asImageBitmap()

    //Log.i("image","Image : ${imageBitmap.width} ${imageBitmap.height}")

    val allAnimateFrames = mutableListOf<MutableList<ImageBitmap>>()
    val columns = imageBitmap.width / frameWidth
    val rows = imageBitmap.height / frameHeight

    for (y in 0..<rows) {

        val animateFrames = mutableListOf<ImageBitmap>()

        for (x in 0..<columns) {
            val frame = createFrame(frameWidth, frameHeight, x, y, imageBitmap)

            animateFrames.add(frame)
        }
        allAnimateFrames.add(animateFrames)
    }
    return allAnimateFrames
}
