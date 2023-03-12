package com.xiaoyv.widget.kts

/**
 * ViewKt
 *
 * @author why
 * @since 2023/3/12
 */

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.widget.listener.SimpleAnimatorListener
import kotlin.reflect.KFunction2

/**
 * ViewKt
 *
 * @author why
 * @since 2023/3/11
 */

val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)

/**
 * 根据 View 获取 Activity
 */
fun View.fetchActivity(): Activity? {
    return context.fetchActivity
}

/**
 * 根据 View 获取 Activity
 */
fun View.fetchFragmentActivity(): FragmentActivity? {
    return context.fetchActivity as? FragmentActivity
}

/**
 * 前景闪烁动画
 */
fun View.flashForegroundAnimation(@ColorInt color: Int) {
    val drawable = ColorDrawable(color)
    ObjectAnimator.ofInt(50, 0, 50, 0, 50, 0)
        .setDuration(1000)
        .apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                foreground = drawable.also {
                    it.alpha = animator.animatedValue as Int
                }
            }
            doOnEnd {
                foreground = null
            }
        }.start()
}

/**
 * 防止过快调用时闪烁问题，150ms 内的连续配置仅保留最后一次的操作
 */
fun <T : View, V, R> T.flashLimit(function: KFunction2<T, V, R>, data: V, minInternal: Long = 150) {
    val cacheHandler = tag
    if (cacheHandler is Runnable) {
        tag = null
        handler?.removeCallbacks(cacheHandler)
    }

    val runnable = Runnable {
        tag = null
        function.invoke(this, data)
    }
    tag = runnable
    handler?.postDelayed(runnable, minInternal)
}

/**
 * 仅监听事件，不拦截
 */
@SuppressLint("ClickableViewAccessibility")
fun View.setTouchActionListener(action: (View, MotionEvent) -> Unit) {
    setOnTouchListener { v, event ->
        action(v, event)
        return@setOnTouchListener false
    }
}

fun View.resetAnimation() {
    clearAnimation()
    animate().cancel()
    alpha = 1f
    scaleX = 1f
    scaleY = 1f
    translationX = 0f
    translationY = 0f
}

fun View.alphaVisibleIn() {
    if (isVisible) return

    resetAnimation()

    alpha = 0f
    isVisible = true
    animate()
        .alpha(1f)
        .setInterpolator(LinearInterpolator())
        .setListener(null)
        .duration = 200
}

fun View.alphaVisibleOut() {
    if (!isVisible) return

    resetAnimation()

    alpha = 1f
    isVisible = true
    animate()
        .alpha(0f)
        .setInterpolator(LinearInterpolator())
        .setListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
                animation.removeAllListeners()
                isVisible = false
            }
        }).duration = 200
}