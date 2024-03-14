@file:Suppress("DEPRECATION")

package com.xiaoyv.blueprint.base

import android.view.Window
import android.view.WindowManager
import com.blankj.utilcode.util.ScreenUtils
import com.gyf.immersionbar.ImmersionBar

/**
 * Class: [BaseConfig]
 *
 * @author why
 * @since 1/3/24
 */
class BaseConfig private constructor() {
    var globalConfig = object : OnConfigActivity {}

    interface OnConfigActivity {
        fun initWindowConfig(activity: BaseActivity, window: Window) {
            // 设置屏幕方向
            ScreenUtils.setPortrait(activity)

            // 窗口参数
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
        }

        fun initBarConfig(activity: BaseActivity, nightMode: Boolean) {
            ImmersionBar.with(activity)
                .transparentStatusBar()
                .transparentNavigationBar()
                .statusBarDarkFont(!nightMode)
                .init()
        }
    }

    companion object {
        val config by lazy { BaseConfig() }
    }
}