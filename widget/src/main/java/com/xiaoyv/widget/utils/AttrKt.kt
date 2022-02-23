package com.xiaoyv.widget.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes

fun Context.getAttrDrawable(@AttrRes attrRes: Int): Drawable? {
    val intArrayOf = intArrayOf(attrRes)
    return obtainStyledAttributes(intArrayOf).let {
        val drawable = it.getDrawable(0)
        it.recycle()
        return@let drawable
    }
}

fun Context.getAttrDimensionPixelSize(@AttrRes attrRes: Int): Int {
    val intArrayOf = intArrayOf(attrRes)
    return obtainStyledAttributes(intArrayOf).let {
        val value = it.getDimensionPixelSize(0, 0)
        it.recycle()
        return@let value
    }
}
