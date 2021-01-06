package com.pinkienort.app.gesturecontroller

import android.graphics.Point
import android.graphics.Rect

operator fun Rect.contains(pos: Point): Boolean {
  return contains(pos.x, pos.y)
}

operator fun Rect.contains(pos: Pair<Int, Int>): Boolean {
  val (x, y) = pos
  return contains(x, y)
}
