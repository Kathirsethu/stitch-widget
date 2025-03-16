package com.stitch.stitchwidgets.utilities

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

internal open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private open inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onClick()
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleClick()
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            onLongClick()
            super.onLongPress(e)
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val diffY = (e2.y).minus(e1?.y ?: 0F)
                val diffX = (e2.x).minus(e1?.x ?: 0F)
                performActionOnFling(
                    diffX,
                    diffY,
                    velocityX,
                    velocityY,
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }
    }

    private fun performActionOnFling(
        diffX: Float,
        diffY: Float,
        velocityX: Float,
        velocityY: Float
    ) {
        val swipeThreshold = 10
        val swipeVelocityThreshold = 10
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
            }
        } else {
            if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                if (diffY > 0) {
                    onSwipeDown()
                } else {
                    onSwipeUp()
                }
            }
        }
    }

    open fun onSwipeRight() {
        //Callback when the view is swiped right
    }

    open fun onSwipeLeft() {
        //Callback when the view is swiped left
    }

    open fun onSwipeUp() {
        //Callback when the view is swiped up
    }

    open fun onSwipeDown() {
        //Callback when the view is swiped down
    }

    private fun onClick() {
        //Callback when the view is clicked
    }

    private fun onDoubleClick() {
        //Callback when the view is double clicked
    }

    private fun onLongClick() {
        //Callback when the view is long clicked
    }
}