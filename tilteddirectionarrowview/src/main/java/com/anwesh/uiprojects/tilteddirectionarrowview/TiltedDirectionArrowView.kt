package com.anwesh.uiprojects.tilteddirectionarrowview

/**
 * Created by anweshmishra on 20/10/20.
 */
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Color
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#4CAF50",
        "#3F51B5",
        "#009688",
        "#03A9F4"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 6
val rot : Float = 90f
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 4.2f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")

