package com.xiaoyv.blueprint

import android.app.Application
import com.xiaoyv.webview.X5WebView

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
        X5WebView.init(this)
    }
}