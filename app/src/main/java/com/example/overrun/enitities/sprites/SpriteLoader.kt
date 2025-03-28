package com.example.overrun.enitities.sprites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

private fun createFrame(frameWidth: UInt, frameHeight: UInt, x: Int, y: Int, imageBitmap: ImageBitmap): ImageBitmap
{
    // Create an ImageBitmap to hold the frame
    val frame = ImageBitmap(frameWidth.toInt(), frameHeight.toInt())

    // Create a Canvas to draw the image onto the frame
    val canvas = Canvas(frame)

    // Draw the image on the canvas
    canvas.drawImageRect(
        image = imageBitmap,
        srcOffset = IntOffset(x * frameWidth.toInt(), y * frameHeight.toInt()),
        srcSize = IntSize(frameWidth.toInt(), frameHeight.toInt()),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(frameWidth.toInt(), frameHeight.toInt()),
        paint = Paint() // pass in a default paint obj
    )

    return frame
}

fun scaleImageBitmap(image: ImageBitmap, scaleFactor: Float): ImageBitmap {
    val width = (image.width * scaleFactor).toInt()
    val height = (image.height * scaleFactor).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(
        image.asAndroidBitmap(), width, height, true
    )

    return resizedBitmap.asImageBitmap()
}

// Each Row is one kind of animation, the column of each row is the frame alternation
// Return a 2D List of ImageBitmap
fun loadSpriteSheet(res: android.content.res.Resources, @DrawableRes resId: Int,
                    frameWidth: UInt, frameHeight: UInt,
                    scaleFactor: Float = 1f): MutableList<MutableList<ImageBitmap>> {

    val options = BitmapFactory.Options().apply {
        inScaled = false  // Disable scaling based on density
    }

    val imageBitmap = BitmapFactory.decodeResource(res, resId, options).asImageBitmap()

    //Log.i("image","Image : ${imageBitmap.width} ${imageBitmap.height}")

    val allAnimateFrames = mutableListOf<MutableList<ImageBitmap>>()
    val columns = imageBitmap.width / frameWidth.toInt()
    val rows = imageBitmap.height / frameHeight.toInt()

    for (y in 0..<rows) {

        val animateFrames = mutableListOf<ImageBitmap>()

        for (x in 0..<columns) {
            var frame = createFrame(frameWidth, frameHeight, x, y, imageBitmap)

            // Scale the frame
            if (scaleFactor != 1f)
            {
                frame = scaleImageBitmap(frame, scaleFactor)
            }

            animateFrames.add(frame)
        }
        allAnimateFrames.add(animateFrames)
    }
    return allAnimateFrames
}

// Return 1D List of Image
fun loadSpriteSheet1D(res: android.content.res.Resources, @DrawableRes resId: Int,
                    frameWidth: UInt, frameHeight: UInt,
                    scaleFactor: Float = 1f): MutableList<ImageBitmap> {

    val options = BitmapFactory.Options().apply {
        inScaled = false  // Disable scaling based on density
    }

    val imageBitmap = BitmapFactory.decodeResource(res, resId, options).asImageBitmap()

    //Log.i("image","Image : ${imageBitmap.width} ${imageBitmap.height}")

    val allAnimateFrames = mutableListOf<ImageBitmap>()
    val columns = imageBitmap.width / frameWidth.toInt()
    val rows = imageBitmap.height / frameHeight.toInt()

    val isInVertical = if (rows > columns) true else false
    val sizes = if (isInVertical) rows else columns

    for (i in 0..<sizes) {

        val x = if (isInVertical) 0 else i
        val y = if (isInVertical) i else 0

        var frame = createFrame(frameWidth, frameHeight, x, y, imageBitmap)

        // Scale the frame
        if (scaleFactor != 1f)
        {
            frame = scaleImageBitmap(frame, scaleFactor)
        }
        allAnimateFrames.add(frame)
    }
    return allAnimateFrames
}

// Overload function for single frame
fun loadSpriteSheet(res: android.content.res.Resources, @DrawableRes resId: Int): ImageBitmap {

    val options = BitmapFactory.Options().apply {
        inScaled = false  // Disable scaling based on density
    }

    return BitmapFactory.decodeResource(res, resId, options).asImageBitmap()
}
