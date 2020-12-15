package com.pinkienort.app.gesturecontroller

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.*


operator fun Rect.contains(pos: Point): Boolean {
    return contains(pos.x, pos.y)
}

operator fun Rect.contains(pos: Pair<Int, Int>): Boolean {
    val (x, y) = pos
    return contains(x, y)
}

class GestureHandler(
    context: Context,
    config: GestureConfig,
    private val listener: OnGestureListener
) : GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    var config: GestureConfig = config
        set(value) {
            field = value
            val ratio = config.clickableAreaRatio * 0.01
            val clickableWidth = ((displaySize.width / 2) * ratio).toInt()
            leftSide = Rect(
                0,
                0,
                clickableWidth,
                displaySize.height
            )
            rightSide = Rect(
                displaySize.width - clickableWidth,
                0,
                displaySize.width,
                displaySize.height
            )
        }

    private val displaySize: Size = context.resources.displayMetrics.run {
        Size(widthPixels, heightPixels)
    }
    private var leftSide = Rect(
        0, 0, displaySize.width / 2, displaySize.height
    )
    private var rightSide = Rect(
        displaySize.width / 2, 0, displaySize.width, displaySize.height
    )

    companion object {
        private const val NO_SWIPE = -1
        private const val SWIPE_TO_LEFT = 0
        private const val SWIPE_TO_RIGHT = 1
    }

    interface OnGestureListener {
        fun onSwipeToLeft()
        fun onSwipeToRight()
        fun onDoubleTapInLeftSide()
        fun onDoubleTapInRightSide()
        fun onLongPressInLeftSide()
        fun onLongPressInRightSide()
        fun onGestureEvent(logMessage: String)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        val pos = e.x.toInt() to e.y.toInt()
        when (pos) {
            in leftSide -> listener.onLongPressInLeftSide()
            in rightSide -> listener.onLongPressInRightSide()
        }
        listener.onGestureEvent("long press: $e")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        when (checkWhetherSwipe(e1, e2, velocityX, velocityY)) {
            SWIPE_TO_LEFT -> listener.onSwipeToLeft()
            SWIPE_TO_RIGHT -> listener.onSwipeToRight()
        }
        return true
    }

    private fun checkWhetherSwipe(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Int {
        val x = e1.x - e2.x
        val y = e1.y - e2.y

        val radian = atan(abs(x) / abs(y))
        val rawAngle = (radian * 180) / PI
        val angle = abs(rawAngle - 90).toFloat()

        val flingDistance = abs(x)
        val velocity = abs(velocityX)

        val direction = if (
            flingDistance > config.flingDistanceAsSwipe &&
            angle < config.swipeDirectionRange &&
            velocity > config.swipeVelocity
        ) {
            if (x > 0) SWIPE_TO_RIGHT else SWIPE_TO_LEFT
        } else {
            NO_SWIPE
        }

        var message = "fling: [dist: $flingDistance, angle:$angle, velocity: $velocity, x:$x, y:$y, vx:$velocityX, vy:$velocityY]\n"
        message += "e1: $e1\n"
        message += "e2: $e2"
        listener.onGestureEvent(message)

        return direction
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val pos = e.x.toInt() to e.y.toInt()
        when (pos) {
            in leftSide -> listener.onDoubleTapInLeftSide()
            in rightSide -> listener.onDoubleTapInRightSide()
        }
        listener.onGestureEvent("double tap: $e")
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return true
    }
}

