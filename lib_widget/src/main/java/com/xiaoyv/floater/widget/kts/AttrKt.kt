package com.xiaoyv.floater.widget.kts

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

fun Context.getAttrDrawable(@AttrRes attrRes: Int, @DrawableRes default: Int = 0): Drawable? {
    val intArrayOf = intArrayOf(attrRes)
    return obtainStyledAttributes(intArrayOf).let {
        val drawable = it.getDrawable(0) ?: ResourcesCompat.getDrawable(resources, default, theme)
        it.recycle()
        return@let drawable
    }
}

fun Context.getAttrDimensionPixelSize(@AttrRes attrRes: Int, default: Int = 0): Int {
    val intArrayOf = intArrayOf(attrRes)
    return obtainStyledAttributes(intArrayOf).let {
        val value = it.getDimensionPixelSize(0, default)
        it.recycle()
        return@let value
    }
}

@ColorInt
fun Context.getAttrColor(@AttrRes attrRes: Int, default: Int = 0): Int {
    val attribute = intArrayOf(attrRes)
    val array = obtainStyledAttributes(0, attribute)
    val color = array.getColor(0, default)
    array.recycle()
    return color
}

@AnyRes
fun Context.getAttrResourceId(@AttrRes attrRes: Int, default: Int = 0): Int {
    val intArrayOf = intArrayOf(attrRes)
    return obtainStyledAttributes(intArrayOf).let {
        val value = it.getResourceId(0, default)
        it.recycle()
        return@let value
    }
}
