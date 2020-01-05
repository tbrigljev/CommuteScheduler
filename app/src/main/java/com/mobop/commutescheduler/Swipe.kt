/* Inspired by and adapted from
https://stackoverflow.com/questions/49754979/capture-events-by-sliding-left-or-right-using-kotlin
 */
package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
/* *************************************************************** */

/* Swipe ********************************************************* */
/* Used for movement detection on commutes edition *************** */
open class OnSwipeTouchListener : View.OnTouchListener {

    private val gestureDetector = GestureDetector(GestureListener())

    fun onTouch(event: MotionEvent): Boolean {
        onClick()
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        private val SWIPE_THRESHOLD = 50 //10
        private val SWIPE_VELOCITY_THRESHOLD = 30 //60

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onTouch(e)
            return true
        }

        override fun onFling(
            e1 : MotionEvent,
            e2 : MotionEvent,
            velocityX : Float,
            velocityY : Float)
                : Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                Log.i("swipemove",
                    "e1 = " +
                            abs(diffX).toString() +
                            " ; e2 = " +
                            abs(diffY).toString() +
                            " ; vX = " +
                            velocityX.toString() +
                            " ; vy = " +
                            velocityY.toString())

                if(abs(diffX) > abs(diffY)){
                    if(abs(diffX) > SWIPE_THRESHOLD &&
                        abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                        //Log.i("Swipe move","ok")
                        if (diffX > 0){
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    // onTouch(e)
                }
            } catch(exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean{
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight(){}

    open fun onSwipeLeft(){}

    open fun onClick(){}
}