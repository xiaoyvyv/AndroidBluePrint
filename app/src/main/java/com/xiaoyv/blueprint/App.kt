package com.xiaoyv.blueprint

import android.app.Application

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
    }
}