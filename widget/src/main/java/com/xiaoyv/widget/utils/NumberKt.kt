package com.xiaoyv.widget.utils

import com.blankj.utilcode.util.Utils

inline val Float.dpi: Int
    get() = AutoSizeKt.dp2px(Utils.getApp(), this)

inline val Int.dpi: Int
    get() = AutoSizeKt.dp2px(Utils.getApp(), this.toFloat())

inline val Float.spi: Int
    get() = AutoSizeKt.sp2px(Utils.getApp(), this)

inline val Int.spi: Int
    get() = AutoSizeKt.sp2px(Utils.getApp(), this.toFloat())