@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.adapt

import android.app.Application
import android.content.res.Resources
import com.blankj.utilcode.util.ScreenUtils
import java.lang.reflect.Field

/**
 * AdaptScreenConfig
 *
 * @author why
 * @since 2022/5/10
 */
class AdaptScreenConfig(
    var mInitAdaptScreen: Boolean = false,
    var mInitDensity: Float = -1f,
    var mInitDensityDpi: Int = 0,
    var mInitScaledDensity: Float = 0f,
    var mInitXdpi: Float = 0f,
    var mInitScreenWidthDp: Int = 0,
    var mInitScreenHeightDp: Int = 0,
    var mInitDesignWidthInDp: Int = 0,
    var mInitDesignHeightInDp: Int = 0,
    var mScreenWidth: Int = 0,
    var mScreenHeight: Int = 0,
    var isExcludeFontScale: Boolean = false,
    var privateFontScale: Float = 0f,
    var isMiui: Boolean = false,
    var mTmpMetricsField: Field? = null,
    var isSupportScreenSizeDP: Boolean = false,
) {

    fun init(
        application: Application,
        adaptScreen: Boolean,
        designWidthInDp: Int = 400,
        designHeightInDp: Int = 1000
    ) {
        val displayMetrics = Resources.getSystem().displayMetrics
        val configuration = Resources.getSystem().configuration

        // 配置初始化
        mInitAdaptScreen = adaptScreen
        mInitDensity = displayMetrics.density
        mInitDensityDpi = displayMetrics.densityDpi
        mInitScaledDensity = displayMetrics.scaledDensity
        mInitXdpi = displayMetrics.xdpi
        mInitScreenWidthDp = configuration.screenWidthDp
        mInitScreenHeightDp = configuration.screenHeightDp
        mInitDesignWidthInDp = designWidthInDp
        mInitDesignHeightInDp = designHeightInDp
        mScreenWidth = ScreenUtils.getScreenWidth()
        mScreenHeight = ScreenUtils.getScreenWidth()

        // MIUI 兼容
        val simpleName = application.resources.javaClass.simpleName
        if ("MiuiResources" == simpleName || "XResources" == simpleName) {
            isMiui = true
            runCatching {
                mTmpMetricsField = Resources::class.java.getDeclaredField("mTmpMetrics")
                mTmpMetricsField?.isAccessible = true
            }.onFailure {
                mTmpMetricsField = null
            }
        }
    }

    companion object {
        @JvmStatic
        val instance: AdaptScreenConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AdaptScreenConfig()
        }
    }
}