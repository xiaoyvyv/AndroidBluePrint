package com.xiaoyv.widget.utils

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes

fun View.getAttrDrawable(@AttrRes attrRes: Int): Drawable? {
    val intArrayOf = intArrayOf(attrRes)
    return context.obtainStyledAttributes(intArrayOf).let {
        val drawable = it.getDrawable(0)
        it.recycle()
        return@let drawable
    }
}