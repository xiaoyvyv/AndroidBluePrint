package com.xiaoyv.floater.widget.kts

import com.blankj.utilcode.util.Utils

inline val Float.dpi: Int
    get() = AutoSizeKt.dp2px(Utils.getApp(), this)
inline val Float.dpf: Float
    get() = AutoSizeKt.dp2px(Utils.getApp(), this).toFloat()

inline val Int.dpi: Int
    get() = AutoSizeKt.dp2px(Utils.getApp(), this.toFloat())
inline val Int.dpf: Float
    get() = AutoSizeKt.dp2px(Utils.getApp(), this.toFloat()).toFloat()

inline val Float.spi: Int
    get() = AutoSizeKt.sp2px(Utils.getApp(), this)
inline val Float.spf: Float
    get() = AutoSizeKt.sp2px(Utils.getApp(), this).toFloat()

inline val Int.spi: Int
    get() = AutoSizeKt.sp2px(Utils.getApp(), this.toFloat())
inline val Int.spf: Float
    get() = AutoSizeKt.sp2px(Utils.getApp(), this.toFloat()).toFloat()

fun Int?.orEmpty(): Int {
    return this ?: 0
}

fun Long?.orEmpty(): Long {
    return this ?: 0L
}

fun Int?.increase(): Int {
    return orEmpty() + 1
}

fun Long?.increase(): Long {
    return orEmpty() + 1
}

fun Int?.zeroReplace(replace: Int): Int {
    return if (orEmpty() == 0) replace else orEmpty()
}

fun Long?.zeroReplace(replace: Long): Long {
    return if (orEmpty() == 0L) replace else orEmpty()
}