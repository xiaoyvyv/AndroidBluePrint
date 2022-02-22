package com.xiaoyv.blueprint

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.xiaoyv.blueprint.localize.LocalizeManager

/**
 * App
 *
 * @author why
 * @since 2021/10/8
 */
class App : BluePrintApp() {
    override fun onCreate() {
        super.onCreate()
        BluePrint.init(this, true)
    }
}