@file:Suppress("unused")

package com.xiaoyv.widget.adapt

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Looper
import android.util.DisplayMetrics
import android.util.SparseArray
import com.blankj.utilcode.util.RomUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import kotlin.math.roundToInt

private val mCache = SparseArray<DisplayMetricsInfo>()

private const val MODE_SHIFT = 30
private const val MODE_MASK = 0x3 shl MODE_SHIFT
private const val MODE_ON_WIDTH = 1 shl MODE_SHIFT
private const val MODE_DEVICE_SIZE = 2 shl MODE_SHIFT

val adaptConfig: AdaptScreenConfig
    get() = AdaptScreenConfig.instance

/**
 * 头条屏幕适配部分场景失效问题兼容
 */
fun Resources.autoConvertDensity(): Resources {
    if (adaptConfig.mInitAdaptScreen.not()) {
        return this
    }
    // 设计图宽度
    val designWidthInDp = adaptConfig.mInitDesignWidthInDp

    // 适配
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return adaptWidth(designWidthInDp)
    } else ThreadUtils.runOnUiThread {
        adaptWidth(designWidthInDp)
    }
    return this
}

/**
 * 根据宽度适配
 */
fun Resources.adaptWidth(designWidth: Int): Resources {
    autoConvertDensity(designWidth.toFloat(), true)
    return this
}

/**
 * 根据高度适配
 */
fun Resources.adaptHeight(designHeight: Int): Resources {
    autoConvertDensity(designHeight.toFloat(), false)
    return this
}

/**
 * 这里是今日头条适配方案的核心代码, 核心在于根据当前设备的实际情况做自动计算并转换
 */
private fun Resources.autoConvertDensity(sizeInDp: Float, isBaseOnWidth: Boolean) {
    val screenSize = if (isBaseOnWidth) ScreenUtils.getScreenWidth()
    else ScreenUtils.getScreenHeight()

    // 初始配置参数
    val mInitScaledDensity = adaptConfig.mInitScaledDensity
    val screenWidth = adaptConfig.mScreenWidth.toFloat()
    val screenHeight = adaptConfig.mScreenHeight.toFloat()

    var key = ((sizeInDp + screenSize) * mInitScaledDensity).roundToInt() and MODE_MASK.inv()
    key = if (isBaseOnWidth) key or MODE_ON_WIDTH else key and MODE_ON_WIDTH.inv()
    key = key or MODE_DEVICE_SIZE

    val displayMetricsInfo = mCache[key]

    val targetDensity: Float
    val targetDensityDpi: Int
    val targetScaledDensity: Float
    val targetXdpi: Float
    val targetScreenWidthDp: Int
    val targetScreenHeightDp: Int

    if (displayMetricsInfo == null) {
        targetDensity = (if (isBaseOnWidth) screenWidth else screenHeight) / sizeInDp

        // 字体缩放
        targetScaledDensity = if (adaptConfig.privateFontScale > 0) {
            targetDensity * adaptConfig.privateFontScale
        } else {
            val systemFontScale = if (adaptConfig.isExcludeFontScale) 1f
            else mInitScaledDensity / adaptConfig.mInitDensity
            targetDensity * systemFontScale
        }
        targetDensityDpi = (targetDensity * 160).roundToInt()
        targetScreenWidthDp = (screenWidth / targetDensity).roundToInt()
        targetScreenHeightDp = (screenHeight / targetDensity).roundToInt()
        targetXdpi = (if (isBaseOnWidth) screenWidth else screenHeight) / sizeInDp

        mCache.put(
            key,
            DisplayMetricsInfo(
                targetDensity,
                targetDensityDpi,
                targetScaledDensity,
                targetXdpi,
                targetScreenWidthDp,
                targetScreenHeightDp
            )
        )
    } else {
        targetDensity = displayMetricsInfo.density
        targetDensityDpi = displayMetricsInfo.densityDpi
        targetScaledDensity = displayMetricsInfo.scaledDensity
        targetXdpi = displayMetricsInfo.xdpi
        targetScreenWidthDp = displayMetricsInfo.screenWidthDp
        targetScreenHeightDp = displayMetricsInfo.screenHeightDp
    }

    setDensity(targetDensity, targetDensityDpi, targetScaledDensity, targetXdpi)
    setScreenSizeDp(targetScreenWidthDp, targetScreenHeightDp)
}


/**
 * 取消适配
 */
fun Resources.cancelAdapt() {
    val initXdpi = adaptConfig.mInitXdpi

    setDensity(
        adaptConfig.mInitDensity,
        adaptConfig.mInitDensityDpi,
        adaptConfig.mInitScaledDensity,
        initXdpi
    )
    setScreenSizeDp(
        adaptConfig.mInitScreenWidthDp,
        adaptConfig.mInitScreenHeightDp
    )
}

/**
 * 给几大 [DisplayMetrics] 赋值
 *
 * @param density       [DisplayMetrics.density]
 * @param densityDpi    [DisplayMetrics.densityDpi]
 * @param scaledDensity [DisplayMetrics.scaledDensity]
 * @param xdpi          [DisplayMetrics.xdpi]
 */
private fun Resources.setDensity(
    density: Float,
    densityDpi: Int,
    scaledDensity: Float,
    xdpi: Float
) {
    val activityDisplayMetrics = displayMetrics
    val appDisplayMetrics = Utils.getApp().resources.displayMetrics
    val systemDisplayMetrics = Resources.getSystem().displayMetrics

    activityDisplayMetrics.setDensity(density, densityDpi, scaledDensity, xdpi)
    appDisplayMetrics.setDensity(density, densityDpi, scaledDensity, xdpi)
    systemDisplayMetrics.setDensity(density, densityDpi, scaledDensity, xdpi)

    //兼容 MIUI
    val activityDisplayMetricsOnMIUI = getMetricsOnMiui(this)
    val appDisplayMetricsOnMIUI = getMetricsOnMiui(Utils.getApp().resources)

    activityDisplayMetricsOnMIUI?.setDensity(density, densityDpi, scaledDensity, xdpi)
    appDisplayMetricsOnMIUI?.setDensity(density, densityDpi, scaledDensity, xdpi)
}

/**
 * 赋值
 *
 * @param density        [DisplayMetrics.density]
 * @param densityDpi     [DisplayMetrics.densityDpi]
 * @param scaledDensity  [DisplayMetrics.scaledDensity]
 * @param xdpi           [DisplayMetrics.xdpi]
 */
private fun DisplayMetrics.setDensity(
    density: Float,
    densityDpi: Int,
    scaledDensity: Float,
    xdpi: Float
) {
    this.density = density
    this.densityDpi = densityDpi
    this.scaledDensity = scaledDensity
    // 副单位设定PT适配
    this.xdpi = xdpi * 72f
}

/**
 * 给 [Configuration] 赋值
 *
 * @param screenWidthDp  [Configuration.screenWidthDp]
 * @param screenHeightDp [Configuration.screenHeightDp]
 */
private fun Resources.setScreenSizeDp(screenWidthDp: Int, screenHeightDp: Int) {
    if (adaptConfig.isSupportScreenSizeDP) {
        configuration.screenWidthDp = screenWidthDp
        configuration.screenHeightDp = screenHeightDp

        val appConfiguration = Utils.getApp().resources.configuration
        appConfiguration.screenWidthDp = screenWidthDp
        appConfiguration.screenHeightDp = screenHeightDp

        val systemConfiguration = Resources.getSystem().configuration
        systemConfiguration.screenWidthDp = screenWidthDp
        systemConfiguration.screenHeightDp = screenHeightDp
    }
}

/**
 * 解决 MIUI 更改框架导致的 MIUI7 + Android5.1.1 上出现的失效问题。
 * 以及极少数基于这部分 MIUI 去掉 ART 然后置入 XPosed 的手机
 */
private fun getMetricsOnMiui(resources: Resources): DisplayMetrics? {
    val mTmpMetricsField = adaptConfig.mTmpMetricsField ?: return null
    if (RomUtils.isXiaomi()) {
        runCatching {
            return mTmpMetricsField.get(resources) as? DisplayMetrics
        }
    }
    return null
}
