package com.aboelatta.universalMediaPreview

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

internal class GestureListener(
    val onTouch: () -> Unit,
    val onSwipeDownTouch: () -> Unit,
) : GestureDetector.SimpleOnGestureListener() {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 150

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffY = e2.y - (e1?.y ?: 0f)
        val diffX = e2.x - (e1?.x ?: 0f)
        if (abs(diffY) > abs(diffX) && abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
                // Swipe down
                onSwipeDownTouch()
            } else {
                onTouch()
            }
        }
        return true
    }
}
