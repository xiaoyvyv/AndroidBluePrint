package com.xiaoyv.floater.widget.listener

import android.animation.Animator
import androidx.annotation.CallSuper

/**
 * SimpleAnimatorListener
 *
 * @author why
 * @since 2023/3/12
 */
open class SimpleAnimatorListener : Animator.AnimatorListener {

    @CallSuper
    override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
        animation.removeListener(this)
        onAnimationStart(animation)
    }

    @CallSuper
    override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
        animation.removeListener(this)
        onAnimationEnd(animation)
    }

    override fun onAnimationStart(animation: Animator) {

    }

    override fun onAnimationEnd(animation: Animator) {

    }

    override fun onAnimationCancel(animation: Animator) {

    }

    override fun onAnimationRepeat(animation: Animator) {

    }
}