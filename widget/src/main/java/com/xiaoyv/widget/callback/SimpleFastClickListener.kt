package com.xiaoyv.widget.callback

import android.view.View

/**
 * OnMultiClickListener
 *
 * @author why
 * @since 2020/11/29
 */
abstract class SimpleFastClickListener(private val interval: Long = 200L) : View.OnClickListener {
    private var lastCallbackTime: Long = 0

    abstract fun onMultiClick(v: View)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()

        if (curClickTime - lastCallbackTime >= interval) {
            onMultiClick(v)
            lastCallbackTime = curClickTime
            return
        }
    }
}

inline fun View.setOnFastLimitClickListener(
    interval: Long = 200L,
    processLongPress: Boolean = true,
    crossinline onMultiClick: (View, Boolean) -> Unit = { _, _ -> }
): SimpleFastClickListener {
    val clickListener = object : SimpleFastClickListener(interval) {
        override fun onMultiClick(v: View) {
            onMultiClick.invoke(v, false)
        }
    }
    setOnClickListener(clickListener)
    if (processLongPress) {
        setOnLongClickListener {
            onMultiClick.invoke(it, true)
            true
        }
    }
    return clickListener
}