package com.xiaoyv.blueprint.base

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.AutoDisposeConverter
import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.blueprint.BluePrint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
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
        mContext?.clear()
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
    protected fun <T : Any> bindTransformer(): ObservableTransformer<T, T> {
        return BluePrint.bindTransformer()
    }


    override fun v2pOnCreate() {}
    override fun v2pOnStart() {}
    override fun v2pOnResume() {}
    override fun v2pOnPause() {}
    override fun v2pOnStop() {}
    override fun v2pOnDestroy() {}
}