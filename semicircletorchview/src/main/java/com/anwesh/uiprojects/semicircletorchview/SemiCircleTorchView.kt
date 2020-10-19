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
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val parts : Int =7
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
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

fun Canvas.drawTorchPath(scale : Float, r : Float, paint : Paint) {
    paint.style = Paint.Style.FILL
    save()
    val path : Path = Path()
    path.moveTo(r, 0f)
    path.arcTo(RectF(-r, -r, r, r), 0f, 180f, false)
    path.lineTo(-2 * r, -r)
    path.lineTo(2 * r, -r)
    path.lineTo(r, 0f)
    clipPath(path)
    drawRect(RectF(-2 * r, -r * scale, 2 * r, 0f), paint)
    restore()
}

fun Canvas.drawSemiCircleTorch(scale : Float, w : Float, h : Float, paint : Paint)  {
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val sf4 : Float = sf.divideScale(3, parts)
    val sf5 : Float = sf.divideScale(4, parts)
    val sf6 : Float = sf.divideScale(5, parts)
    val size : Float = Math.min(w, h) / sizeFactor
    val r : Float = size / 2
    save()
    translate(w / 2, h / 2)
    rotate(rot * sf6)
    paint.style = Paint.Style.STROKE
    drawArc(RectF(-r, -r, r, r), 0f, 180f * sf1, false, paint)
    drawLine(-r, 0f, -r -r * sf2, -r * sf2, paint)
    drawLine(-2 * r, -r, -2 * r + 4 * r * sf3, -r, paint)
    drawLine(2 * r, -r, 2 * r - r * sf4, -r + r * sf4, paint)
    drawTorchPath(sf5, r, paint)
    restore()
}

fun Canvas.drawSCTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSemiCircleTorch(scale, w, h, paint)
}

class SemiCircleTorchView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}