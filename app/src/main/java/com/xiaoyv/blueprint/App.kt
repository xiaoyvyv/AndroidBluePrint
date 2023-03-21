package com.xiaoyv.blueprint

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.webview.X5WebView
import com.xiaoyv.webview.helper.X5InstallHelper
import com.xiaoyv.widget.adapt.AdaptScreenConfig
import com.xiaoyv.widget.adapt.autoConvertDensity
import kotlin.random.Random

/**
 * App
 *
 * @author why
 * @since 2021/10/8
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        BluePrint.init(this, true)
//        X5InstallHelper.init(this)
        AdaptScreenConfig.instance.init(this, true)

        X5WebView.getResourcesProxy = {
            it.autoConvertDensity()
        }
    }

    override fun getPackageName(): String {
        val exception = Exception()
        val string = exception.stackTraceToString()
        Log.e("ttttt", string)

        if (string.contains("org.chromium")) {
            return "com.android.browser"
        }
        return super.getPackageName()
    }
}