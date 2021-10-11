package com.xiaoyv.blueprint

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.AutoDispose
import autodispose2.AutoDisposeConverter
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.blueprint.base.IBaseView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig

/**
 * BluePrint
 *
 * @author why
 * @since 2021/10/8
 */
object BluePrint {
    const val MAX_WIDTH_DP = 375f

    @JvmStatic
    fun init(application: Application) {
        Utils.init(application)
        if (ProcessUtils.isMainProcess()) {
            initRxError()
            initAutoSize(application)
        }
    }

    /**
     * RxJava 未捕获的异常
     */
    private fun initRxError() {
        RxJavaPlugins.setErrorHandler {
            LogUtils.e(it)
        }
    }

    /**
     * 屏幕分辨率适配
     *
     * @param application application
     */
    private fun initAutoSize(application: Application) {
        AutoSize.checkAndInit(application)
        AutoSizeConfig.getInstance().designWidthInDp = MAX_WIDTH_DP.toInt()
    }


    /**
     * 统一线程处理
     *
     * @param <O> 指定的泛型类型
     * @return ObservableTransformer
     */
    @JvmStatic
    fun <T : Any> bindTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable: Observable<T> ->
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 绑定生命周期
     *
     * @param <T> T
     * @return T
     */
    @JvmStatic
    fun <T : Any> bindLifecycle(lifecycleOwner: LifecycleOwner): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
        )
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