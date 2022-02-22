package com.xiaoyv.widget.utils

import com.blankj.utilcode.util.Utils
import me.jessyan.autosize.utils.AutoSizeUtils

inline val Float.dpi: Int
    get() = AutoSizeUtils.dp2px(Utils.getApp(), this)

inline val Int.dpi: Int
    get() = AutoSizeUtils.dp2px(Utils.getApp(), this.toFloat())

inline val Float.spi: Int
    get() = AutoSizeUtils.sp2px(Utils.getApp(), this)

inline val Int.spi: Int
    get() = AutoSizeUtils.sp2px(Utils.getApp(), this.toFloat())