package com.xiaoyv.widget.callback

import android.view.View

/**
 * OnMultiClickListener
 *
 * @author why
 * @since 2020/11/29
 */
abstract class SimpleFastClickListener(private val interval: Long = 200L) : View.OnClickListener {
    private var lastClickTime: Long = 0

    abstract fun onMultiClick(v: View)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()

        if (curClickTime - lastClickTime >= interval) {
            lastClickTime = 0
            onMultiClick(v)
            return
        }
        lastClickTime = curClickTime
    }
}

inline fun View.setOnFastLimitClickListener(
    interval: Long = 200L,
    processLongPress: Boolean = true,
    crossinline onMultiClick: (View) -> Unit = {}
): SimpleFastClickListener {
    val clickListener = object : SimpleFastClickListener(interval) {
        override fun onMultiClick(v: View) {
            onMultiClick.invoke(v)
        }
    }
    setOnClickListener(clickListener)
    if (processLongPress) {
        setOnLongClickListener {
            onMultiClick.invoke(it)
            true
        }
    }
    return clickListener
}