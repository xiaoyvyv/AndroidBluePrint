package com.xiaoyv.widget.kts

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * AutoSizeUtils
 */
object AutoSizeKt {
    @JvmStatic
    fun dp2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun sp2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun pt2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PT,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun in2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_IN,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun mm2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }
}
