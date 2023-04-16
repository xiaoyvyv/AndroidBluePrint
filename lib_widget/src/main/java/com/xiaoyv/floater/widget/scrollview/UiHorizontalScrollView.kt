@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.floater.widget.scrollview

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

/**
 * UiHorizontalScrollView
 *
 * @author why
 * @since 2022/1/25
 */
class UiHorizontalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : HorizontalScrollView(context, attrs) {

    var onScrollChanged: (Int, Int, Int, Int) -> Unit = { _, _, _, _ -> }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChanged.invoke(l, t, oldl, oldt)
    }
}