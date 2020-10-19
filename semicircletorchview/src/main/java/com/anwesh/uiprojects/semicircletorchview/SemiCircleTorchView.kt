package com.anwesh.uiprojects.semicircletorchview

/**
 * Created by anweshmishra on 19/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.app.Activity
import android.content.Context

val parts : Int =7
val scGap : Float = 0.02f / parts
val strokFactor : Float = 90f
val sizeFactor : Float = 8.9f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
val colors : Array<Int> = arrayOf(
        "#F44336",
        "#4CAF50",
        "#3F51B5",
        "#009688",
        "#03A9F4"
).map {
    Color.parseColor(it)
}.toTypedArray()
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
