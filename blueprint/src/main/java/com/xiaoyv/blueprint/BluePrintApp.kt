package com.xiaoyv.blueprint

import android.app.Application

/**
 * BluePrintApp
 *
 * @author why
 * @since 2021/10/8
 */
open class BluePrintApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BluePrint.init(this)
    }
}