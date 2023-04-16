package com.xiaoyv.floater.widget.kts

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

/**
 * AnimationKt
 *
 * @author why
 * @since 2023/3/7
 */
fun rotateAnimation(duration: Long = 1500, isClockwise: Boolean = true): RotateAnimation {
    val animation = RotateAnimation(
        if (isClockwise) 0.0f else 360.0f,
        if (isClockwise) 360.0f else 0.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    animation.repeatCount = Animation.INFINITE
    animation.duration = duration
    animation.interpolator = LinearInterpolator()
    return animation
}
