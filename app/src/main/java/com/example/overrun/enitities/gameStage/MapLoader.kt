










package com.example.overrun.enitities.gameStage

import android.content.Context

fun Context.readMapFileInto2DIntArray(fileName: String): Array<IntArray>
{
    val lines = mutableListOf<IntArray>()

    assets.open(fileName).bufferedReader().useLines { fileLines ->
        fileLines.forEach { line ->
            val numbers = line.split(" ")       // Split by space
                .filter { it.isNotBlank() }               // Remove empty elements
                .map { it.toInt() }                       // Convert to Int
                .toIntArray()

            if (numbers.size > 0)
            {
                lines.add(numbers)
            }
        }
    }

    return lines.toTypedArray()
}