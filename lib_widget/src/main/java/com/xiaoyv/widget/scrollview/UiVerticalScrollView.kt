@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.scrollview

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * UiHorizontalScrollView
 *
 * @author why
 * @since 2022/1/25
 */
class UiVerticalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : NestedScrollView(context, attrs) {

    var onScrollChanged: (Int, Int, Int, Int) -> Unit = { _, _, _, _ -> }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChanged.invoke(l, t, oldl, oldt)
    }
}