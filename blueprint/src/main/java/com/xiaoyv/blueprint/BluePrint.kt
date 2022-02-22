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
import com.xiaoyv.blueprint.exception.RxExceptionHandler
import com.xiaoyv.blueprint.exception.RxGlobalExceptionHandler
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
    @JvmOverloads
    fun init(application: Application, adaptScreen: Boolean = false) {
        Utils.init(application)
        if (ProcessUtils.isMainProcess()) {
            initRxError()

            // 是否适配屏幕
            if (adaptScreen) {
                initAutoSize(application)
            }
        }
    }

    /**
     * RxJava 未捕获的异常
     */
    private fun initRxError() {
        // 设置全局异常处理器
        RxExceptionHandler.setExceptionHandler(RxGlobalExceptionHandler())

        // RxJava 未捕获的异常
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
        AutoSizeConfig.getInstance()
            .setExcludeFontScale(true)
            .designWidthInDp = MAX_WIDTH_DP.toInt()
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
     * 统一线程处理 订阅在 IO 线程，观察在主线程
     */
    fun <T : Any> schedulerTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 统一线程处理 订阅、观察都在主线程
     */
    fun <T : Any> schedulerMainTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 统一线程处理 订阅、观察都在 IO 线程
     */
    fun <T : Any> schedulerIOTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable: Observable<T> ->
            observable.subscribeOn(
                Schedulers.io()
            ).observeOn(Schedulers.io())
        }
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