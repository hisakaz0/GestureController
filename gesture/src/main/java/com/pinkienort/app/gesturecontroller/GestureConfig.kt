package com.pinkienort.app.gesturecontroller

class GestureConfig(
   private val map: Map<String, Any?>
) {
   companion object {
     val DEFAULT = GestureConfig(mapOf(
       "flingDistanceAsSwipe" to 300,
       "swipeDirectionRange" to 30,
       "swipeVelocity" to 1000,
       "clickableAreaRatio" to 90
     ))
   }

  val flingDistanceAsSwipe: Int by map
  val swipeDirectionRange: Int by map
  val swipeVelocity: Int by map
  val clickableAreaRatio: Int by map

  override fun toString(): String {
     return "$map"
  }
}
