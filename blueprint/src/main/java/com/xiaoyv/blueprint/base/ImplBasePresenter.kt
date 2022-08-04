@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.content.Context
import androidx.lifecycle.LifecycleOwner
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
}
