package com.xiaoyv.widget.custom

import android.content.Context
import android.view.View

interface InEditModeCompat {
    fun initInApp(appContext: Context)
    fun initInEditMode(editorContext: Context)

    companion object {
        @JvmStatic
        fun init(view: View) {
            if (view is InEditModeCompat) {
                if (view.isInEditMode) {
                    view.initInEditMode(view.context)
                } else {
                    view.initInApp(view.context)
                }
            } else throw IllegalStateException("View: ${view.javaClass.name} 未实现 InEditModeCompat 接口")
        }
    }
}