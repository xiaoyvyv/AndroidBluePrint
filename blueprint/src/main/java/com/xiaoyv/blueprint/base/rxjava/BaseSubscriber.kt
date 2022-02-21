package com.xiaoyv.blueprint.base.rxjava

import autodispose2.ObservableSubscribeProxy
import com.blankj.utilcode.util.StringUtils
import com.google.gson.JsonParseException
import com.xiaoyv.blueprint.R
import com.xiaoyv.blueprint.exception.RxException
import com.xiaoyv.blueprint.exception.RxExceptionHandler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import java.net.SocketTimeoutException

/**
 * BaseSubscriber 基础订阅者
 *
 * @author why
 * @since 2021/12/10
 */
abstract class BaseSubscriber<T> : DisposableObserver<T>() {
    override fun onStart() {}

    override fun onComplete() {}

    override fun onNext(t: T) {
        try {
            onSuccess(t)
        } catch (e: Throwable) {
            onError(e)
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        runCatching {
            when (e) {
                // RxException
                is RxException -> {
                    onError(e)
                }
                // 超时
                is SocketTimeoutException -> {
                    // 超时异常更换文案
                    val errMsg = StringUtils.getString(R.string.bp_common_net_timeout)
                    onError(SocketTimeoutException(errMsg).toRxException())
                }
                // 客户端代码相关异常
                is JsonParseException -> clientError(e)
                is NullPointerException -> clientError(e)
                // 其他
                else -> {
                    // 全局异常处理，转为 RxException
                    onError(RxExceptionHandler.handleException(e))
                }
            }
        }
    }

    /**
     * 出错
     *
     * @param e exception
     */
    abstract fun onError(e: RxException)

    /**
     * 安全版的[.onNext],自动做了try-catch
     *
     * @param t t
     */
    abstract fun onSuccess(t: T)

    /**
     * 捕获到的客户端异常
     */
    private fun clientError(e: Exception) {
        val errMsg = StringUtils.getString(
            R.string.bp_common_client_error,
            e.javaClass.simpleName
        )
        onError(Exception(errMsg, e).toRxException())
    }


    /**
     * 转为 RxException
     */
    private fun Exception.toRxException(): RxException {
        return RxExceptionHandler.handleException(this)
    }
}


/**
 * Kotlin 扩展风格版本
 */
inline fun <R : Any> ObservableSubscribeProxy<R>.subscribes(
    crossinline onError: (e: RxException) -> Unit = { _ -> },
    crossinline onSuccess: (t: R) -> Unit = { _ -> },
): BaseSubscriber<R> {
    val subscriber = object : BaseSubscriber<R>() {
        override fun onError(e: RxException) {
            onError.invoke(e)
        }

        override fun onSuccess(t: R) {
            onSuccess.invoke(t)
        }
    }
    this.subscribe(subscriber)
    return subscriber
}


/**
 * Kotlin 扩展风格版本
 */
inline fun <R : Any> Observable<R>.subscribes(
    crossinline onError: (e: RxException) -> Unit = { _ -> },
    crossinline onSuccess: (t: R) -> Unit = { _ -> },
): BaseSubscriber<R> {
    val subscriber = object : BaseSubscriber<R>() {
        override fun onError(e: RxException) {
            onError.invoke(e)
        }

        override fun onSuccess(t: R) {
            onSuccess.invoke(t)
        }
    }
    this.subscribe(subscriber)
    return subscriber
}


