@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package com.xiaoyv.widget.kts

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.xiaoyv.floater.widget.scrollview.NestedOverScrollDecorAdapter
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

/**
 * setTextAppearanceCompat
 */
fun TextView.setTextAppearanceCompat(@AnyRes resId: Int) {
    setTextAppearance(resId)
}

/**
 * View`s parent view
 */
fun <T : ViewGroup> View?.parentView(): T? {
    val view = this ?: return null
    return view.parent as? T
}

/**
 * remove self from parent
 */
fun View?.removeFromParent() {
    val view = this ?: return
    view.parentView<ViewGroup>()?.removeView(view)
}

/**
 * 由于自定义 View 中的一些简化拓展方法
 */
fun View.getDpx(dp: Float): Int = if (isInEditMode) (dp * 3).toInt() else dp.dpi

fun View.getSpx(sp: Float): Int = if (isInEditMode) (sp * 3).toInt() else sp.spi

@ColorInt
fun View.getColor(@ColorRes colorResId: Int): Int =
    ResourcesCompat.getColor(resources, colorResId, context?.theme)

fun View.getDrawable(@DrawableRes drawableResId: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, drawableResId, context?.theme)

fun View.getString(@StringRes stringResId: Int): String = resources.getString(stringResId)

fun View.getStatusBarHeight() = if (isInEditMode) 60 else BarUtils.getStatusBarHeight()

fun ImageView.setImageTintColorRes(@ColorRes colorResId: Int) {
    imageTintList = ColorStateList.valueOf(getColor(colorResId))
}

fun ImageView.setImageTintColorInt(@ColorInt color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}

/**
 * 越界动画
 */
fun RecyclerView.overScrollV(): IOverScrollDecor =
    OverScrollDecoratorHelper.setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

fun RecyclerView.overScrollH(): IOverScrollDecor =
    OverScrollDecoratorHelper.setUpOverScroll(
        this,
        OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
    )

fun NestedScrollView.overScrollV(): IOverScrollDecor =
    VerticalOverScrollBounceEffectDecorator(NestedOverScrollDecorAdapter(this))