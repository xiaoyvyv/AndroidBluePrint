package com.xiaoyv.blueprint

import android.app.Application
import com.xiaoyv.webview.X5WebView
import com.xiaoyv.widget.adapt.autoConvertDensity

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
        com.xiaoyv.widget.adapt.AdaptScreenConfig.instance.init(this, true)

        X5WebView.getResourcesProxy = {
            it.autoConvertDensity()
        }
    }

}