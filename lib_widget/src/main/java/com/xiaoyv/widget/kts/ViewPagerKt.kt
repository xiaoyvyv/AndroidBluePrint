package com.xiaoyv.widget.kts

import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


/**
 * 调整 VP2 OverScrollModel 为 OVER_SCROLL_NEVER
 */
fun ViewPager2.clearOverScrollModel() {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.overScrollMode = View.OVER_SCROLL_NEVER
    }
}

/**
 * 调整 VP2 上下滑动时，左右翻页触发的灵敏度
 */
fun ViewPager2.adjustScrollSensitivity(float: Float = 2f) {
    runCatching {
        val viewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            .also {
                it.isAccessible = true
            }

        val recyclerView = viewField.get(this) as? RecyclerView ?: return
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop").also {
            it.isAccessible = true
        }

        val vc = ViewConfiguration.get(context)
        val touchSlop = vc.scaledTouchSlop * float

        touchSlopField.set(recyclerView, (touchSlop * float).toInt())
    }
}