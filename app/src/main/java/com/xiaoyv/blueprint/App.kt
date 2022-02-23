package com.xiaoyv.blueprint

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