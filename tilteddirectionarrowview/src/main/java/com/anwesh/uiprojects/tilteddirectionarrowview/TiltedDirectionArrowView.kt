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
import android.graphics.RectF
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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawArrowPathRect(sf4 : Float, tSize : Float, paint : Paint) {
    save()
    val path : Path = Path()
    path.moveTo(0f, tSize / 2)
    path.lineTo(tSize / 2, tSize / 2)
    path.lineTo(tSize, tSize)
    path.lineTo(0f, -tSize)
    path.lineTo(-tSize, tSize)
    path.lineTo(-tSize / 2, tSize / 2)
    path.lineTo(0f, tSize / 2)
    clipPath(path)
    drawRect(RectF(-tSize, tSize - 2 * tSize * sf4, tSize, tSize), paint)
    restore()
}

fun Canvas.drawTiltedDirectionArrow(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val sf4 : Float = sf.divideScale(3, parts)
    val sf5 : Float = sf.divideScale(4, parts)
    val size : Float = Math.min(w, h) / sizeFactor
    val tSize : Float = size / 2
    save()
    translate(w / 2, h / 2)
    rotate(90f * sf5)
    for (j in 0..1) {
        save()
        scale(1f - 2 * j, 1f)
        drawLine(0f, tSize / 2, tSize * 0.5f * sf1, tSize / 2, paint)
        drawLine(tSize / 2, tSize / 2, tSize * 0.5f * (1 + sf2), tSize * 0.5f * (1 + sf2), paint)
        drawLine(tSize, tSize, tSize * (1 - sf3), tSize - 2 * tSize * sf3, paint)
        restore()
    }
    drawArrowPathRect(sf4, tSize, paint)
    restore()
}

fun Canvas.drawTDANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawTiltedDirectionArrow(scale, w, h, paint)
}

class TiltedDirectionArrowView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float=  0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TDANode(var i : Int, val state : State = State()) {

        private var next : TDANode? = null
        private var prev : TDANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = TDANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTDANode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TDANode {
            var curr : TDANode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TiltedDirectionArrow(var i : Int) {

        private var curr : TDANode = TDANode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TiltedDirectionArrowView) {

        private val animator : Animator = Animator(view)
        private val tda : TiltedDirectionArrow = TiltedDirectionArrow(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            tda.draw(canvas, paint)
            animator.animate {
                tda.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tda.startUpdating {
                animator.start()
            }
        }
    }
}