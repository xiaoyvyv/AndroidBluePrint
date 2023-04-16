package com.xiaoyv.floater.widget.listener

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * SimpleOnDoubleTapListener
 *
 * @author why
 * @since 2023/3/12
 */
interface SimpleOnDoubleTapListener : GestureDetector.OnDoubleTapListener {
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }
}