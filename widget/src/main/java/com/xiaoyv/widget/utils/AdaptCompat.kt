package com.xiaoyv.widget.utils

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Looper
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import me.jessyan.autosize.AutoSizeCompat

object AdaptCompat {
    /**
     * 按宽度适配 固定值
     */
    const val ADAPT_WIDTH_DP = 375f

    /**
     * 按宽度适配 固定值而计算出的对应的高度
     * 用于横屏适配 UI 大小
     */
    var ADAPT_HEIGHT_DEPEND_WIDTH_DP = 0f

    /**
     * 是否适配，以下几个方法的开关
     */
    var ENABLE_AUTO_RESOURCE_CONVERT = false
}

private val Resources.isPortrait: Boolean
    get() = this.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun View.autoConvertDensity(): Resources {
    return resources.autoConvertDensity()
}

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun Activity.autoConvertDensity(): Resources {
    return resources.autoConvertDensity()
}

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun Resources.autoConvertDensity(): Resources {
    // 解决 AutoSize 横屏时对话框显示状态，切后台再切回前台导致的适配失效问题
    if (Looper.myLooper() == Looper.getMainLooper()) {
        adaptScreen()
    } else ThreadUtils.runOnUiThread {
        adaptScreen()
    }
    return this
}

private fun Resources.adaptScreen() {
    if (!AdaptCompat.ENABLE_AUTO_RESOURCE_CONVERT) {
        return
    }
    if (isPortrait) {
        AutoSizeCompat.autoConvertDensity(this, AdaptCompat.ADAPT_WIDTH_DP, true)
    } else {
        val adaptHeight =
            if (AdaptCompat.ADAPT_HEIGHT_DEPEND_WIDTH_DP == 0f) AdaptCompat.ADAPT_WIDTH_DP
            else AdaptCompat.ADAPT_HEIGHT_DEPEND_WIDTH_DP
        AutoSizeCompat.autoConvertDensity(this, adaptHeight, true)
    }
}