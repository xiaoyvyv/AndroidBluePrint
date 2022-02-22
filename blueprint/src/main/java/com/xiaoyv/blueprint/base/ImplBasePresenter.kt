@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.AutoDisposeConverter
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.base.rxjava.BaseSubscriber
import com.xiaoyv.blueprint.base.rxjava.subscribes
import com.xiaoyv.blueprint.exception.RxException
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import java.lang.ref.WeakReference

/**
 * ImplBasePresenter
 *
 * @author why
 * @since 2020/11/29
 */
open class ImplBasePresenter<V : IBaseView> : IBasePresenter {
    private var mBaseView: WeakReference<V>? = null
    private var mContext: WeakReference<Context>? = null

    /**
     * 注入V层的LifeCycleOwner,这样在P层也能处理生命周期变化
     */
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * 获取 View 层
     */
    protected val requireView: V
        get() = getView()

    /**
     * 获取 Context
     */
    protected val requireContext: Context
        get() = getContext()


    fun getView(): V = mBaseView?.get() ?: throw  RuntimeException("未绑定 V 层")

    fun getContext(): Context = mContext?.get() ?: throw  RuntimeException("未绑定 V 层")

    /**
     * view，context绑定
     *
     * @param view    iView
     * @param context context
     */
    fun attachView(view: V, context: Context) {
        mBaseView = WeakReference(view)
        mContext = WeakReference(context)
    }

    /**
     * 解绑
     */
    fun detachView() {
        mBaseView?.clear()
        mBaseView = null

        mContext?.clear()
        mContext = null
    }

    override fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    override fun onLifecycleChanged(owner: LifecycleOwner, event: Lifecycle.Event) {}

    /**
     * 绑定生命周期
     *
     * @param <O> o
     * @return o
     */
    fun <T : Any> bindLifecycle(): AutoDisposeConverter<T> {
        return BluePrint.bindLifecycle(
            lifecycleOwner ?: throw NullPointerException("lifecycleOwner == null")
        )
    }

    /**
     * 统一线程处理
     */
    fun <T : Any> bindTransformer(): ObservableTransformer<T, T> {
        return BluePrint.bindTransformer()
    }


    override fun v2pOnCreate() {}
    override fun v2pOnStart() {}
    override fun v2pOnResume() {}
    override fun v2pOnPause() {}
    override fun v2pOnStop() {}
    override fun v2pOnDestroy() {}
}


/**
 * 根据 ImplBasePresenter 同时绑定 线程切换 和 生命周期
 *
 * @param presenter 当前 P 层
 * @param autoHideLoading 默认接口请求结束会自动关闭 Loading 对话框（如果存在显示的情况）
 * @param onError 错误回调
 * @param onSuccess 正常回调
 */
inline fun <R : Any, V : IBaseView> Observable<R>.subscribesWithPresenter(
    presenter: ImplBasePresenter<V>,
    autoHideLoading: Boolean = true,
    crossinline onError: (e: RxException) -> Unit = { _ -> },
    crossinline onSuccess: (t: R) -> Unit = { _ -> },
): BaseSubscriber<R> {
    return this.compose(presenter.bindTransformer())
        .to(presenter.bindLifecycle())
        .subscribes(
            onError = {
                if (autoHideLoading) {
                    presenter.getView().p2vHideLoading()
                }
                onError.invoke(it)
            },
            onSuccess = {
                if (autoHideLoading) {
                    presenter.getView().p2vHideLoading()
                }
                onSuccess.invoke(it)
            })
}