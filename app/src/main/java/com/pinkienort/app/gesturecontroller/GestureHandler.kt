package com.pinkienort.app.gesturecontroller

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.*

class GestureHandler(
    var config: GestureConfig,
    private val listener: OnGestureListener
) : GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    companion object {
        private const val NO_SWIPE = -1
        private const val SWIPE_TO_LEFT = 0
        private const val SWIPE_TO_RIGHT = 1
        private const val TAG = "GestureHandler"
    }

    interface OnGestureListener {
        fun onSwipeToLeft()
        fun onSwipeToRight()
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

        val message = "fling: [dist: $flingDistance, angle:$angle, velocity: $velocity, x:$x, y:$y, vx:$velocityX, vy:$velocityY]"
        listener.onGestureEvent(message)
        Log.d(TAG, message)

        return direction
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return true
    }
}