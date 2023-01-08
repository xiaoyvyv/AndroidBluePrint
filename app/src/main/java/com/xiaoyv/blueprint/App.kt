package com.xiaoyv.blueprint

import android.app.Application
import com.xiaoyv.webview.helper.X5InstallHelper

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
        X5InstallHelper.init(this)
    }
}