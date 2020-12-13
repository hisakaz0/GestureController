package com.pinkienort.app.gesturecontroller

class GestureConfig(
   private val map: Map<String, Any?>
) {
   val flingDistanceAsSwipe: Int by map
   val swipeDirectionRange: Int by map
   val swipeVelocity: Int by map

   override fun toString(): String {
      return "$map"
   }
}
