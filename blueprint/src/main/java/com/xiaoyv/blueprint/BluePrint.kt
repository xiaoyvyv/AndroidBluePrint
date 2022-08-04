@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.blueprint.base.IBaseView
import com.xiaoyv.blueprint.exception.RxExceptionHandler
import com.xiaoyv.blueprint.exception.RxGlobalExceptionHandler
import com.xiaoyv.blueprint.json.GsonParse
import com.xiaoyv.widget.adapt.AdaptScreenConfig


/**
 * BluePrint
 *
 * @author why
 * @since 2021/10/8
 */
object BluePrint {
    @JvmStatic
    @JvmOverloads
    fun init(application: Application, adaptScreen: Boolean = false, lifecycleObserver: DefaultLifecycleObserver? = null) {
        Utils.init(application)
        if (ProcessUtils.isMainProcess()) {
            initRxError()
            // 是否适配屏幕
            initAutoSize(application, adaptScreen)

            // Application 生命周期监听
            ProcessLifecycleOwner.get().lifecycle.apply {
                addObserver(BluePrintObserver())
                lifecycleObserver?.let { addObserver(it) }
            }

            // 将 null 为空的情况下会优先读取默认值，没有再设置字符串类型转为 ""
            // 将 null List 类型转为 []，为空的情况下会优先读取默认值，没有再设置为 List empty对象
            // 将 null Map 类型转为 {}，为空的情况下会优先读取默认值，没有再设置为 Map empty对象
            GsonUtils.setGsonDelegate(GsonParse.GSON)
        }
    }

    /**
     * RxJava 未捕获的异常
     */
    private fun initRxError() {
        // 设置全局异常处理器
        RxExceptionHandler.setExceptionHandler(RxGlobalExceptionHandler())
    }

    /**
     * 屏幕分辨率适配
     */
    private fun initAutoSize(application: Application, adaptScreen: Boolean) {
        AdaptScreenConfig.instance.init(application, adaptScreen)
    }


    /**
     * 验证 MVP 是否实现了 V 层接口，否则抛出异常
     *
     * 只在 DEBUG 环境验证，在开发阶段便于提示开发者编码问题。
     */
    @JvmStatic
    fun checkV(clazz: Class<*>): Boolean {
        if (!AppUtils.isAppDebug()) {
            return true
        }
        val interfaces = clazz.interfaces
        if (interfaces.isNullOrEmpty()) {
            return false
        }
        for (i in interfaces.indices) {
            val itemClz = interfaces[i]
            val itemClzInterfaces = itemClz.interfaces
            return if (itemClzInterfaces.isNullOrEmpty()) {
                itemClz == IBaseView::class.java
            } else {
                checkV(itemClz)
            }
        }
        return false
    }
}