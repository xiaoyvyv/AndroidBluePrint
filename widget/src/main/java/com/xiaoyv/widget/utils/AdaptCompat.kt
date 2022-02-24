package com.xiaoyv.widget.utils

import android.app.Activity
import android.content.res.Resources
import android.os.Looper
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils
import me.jessyan.autosize.AutoSizeCompat


/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun View.autoConvertDensity(sizeInDp: Float): Resources {
    return resources.autoConvertDensity(sizeInDp)
}

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun Activity.autoConvertDensity(sizeInDp: Float): Resources {
    return resources.autoConvertDensity(sizeInDp)
}

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun Resources.autoConvertDensity(sizeInDp: Float): Resources {
    // 解决 AutoSize 横屏时对话框显示状态，切后台再切回前台导致的适配失效问题
    if (Looper.myLooper() == Looper.getMainLooper()) {
        AutoSizeCompat.autoConvertDensity(this, sizeInDp, ScreenUtils.isPortrait())
    } else ThreadUtils.runOnUiThread {
        AutoSizeCompat.autoConvertDensity(this, sizeInDp, ScreenUtils.isPortrait())
    }
    return this
}