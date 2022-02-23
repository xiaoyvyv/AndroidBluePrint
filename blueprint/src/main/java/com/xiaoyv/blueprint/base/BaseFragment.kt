@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import autodispose2.AutoDisposeConverter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.base.rxjava.event.RxEvent
import com.xiaoyv.blueprint.databinding.BpFragmentRootBinding
import com.xiaoyv.blueprint.rxbus.RxBus
import com.xiaoyv.widget.dialog.UiLoadingDialog
import com.xiaoyv.widget.stateview.StateViewImpl
import io.reactivex.rxjava3.core.ObservableTransformer
import java.lang.ref.WeakReference

/**
 * BaseFragment
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseFragment : Fragment(), IBaseView, (StateView, View) -> Unit {
    private lateinit var rootBinding: BpFragmentRootBinding
    protected lateinit var hostActivity: FragmentActivity

    private var loading: UiLoadingDialog? = null

    private var reference: WeakReference<StateView>? = null
    private var stateViewImpl: StateViewImpl? = null

    var rootView: View? = null

    /**
     * 懒加载是否完成
     */
    var isLazyLoaded = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostActivity = requireActivity()

        arguments?.also {
            initArgumentsData(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 设置视图
        if (rootView == null) {
            rootBinding = BpFragmentRootBinding.inflate(layoutInflater, container, false)
            rootBinding.flRoot.addView(createContentView())
            rootView = rootBinding.root
        }

        // 初始化相关回调
        initBaseView()
        initView()
        return rootView
    }

    private fun initBaseView() {
        loading = UiLoadingDialog()
        loading?.isCancelable = false

        // 状态布局
        stateViewImpl = object : StateViewImpl(hostActivity) {
            override fun onCreateStateView(): StateView {
                var stateView = reference?.get()
                if (stateView != null) {
                    return stateView
                }

                stateView = createStateView(hostActivity,  rootBinding.content, this@BaseFragment)
                reference = WeakReference(stateView)
                return stateView
            }
        }
    }

    override fun invoke(p1: StateView, p2: View) {
        p2vClickStatusView(p1, p2)
    }

    protected abstract fun createContentView(): View?
    protected open fun initArgumentsData(arguments: Bundle) {}
    protected abstract fun initView()
    protected abstract fun initData()
    protected open fun initEvent() {}
    protected open fun initListener() {}
    protected open fun initFinish() {}

    /**
     * Androidx 版本的 Fragment 弃用了 setUserVisibleHint()
     * 所以懒加载通过 在可见时会调用 onResume() 实现。
     *
     * 增加了Fragment是否可见的判断
     */
    override fun onResume() {
        super.onResume()

        // 初始化后，处于显示时，的其他非首次回调
        if (isLazyLoaded && !isHidden) {
            onResumeExceptFirst()
        }

        if (!isLazyLoaded && !isHidden) {
            LogUtils.i("${javaClass.simpleName}_LazyLoad")
            initData()
            initEvent()
            initListener()
            initFinish()
            isLazyLoaded = true
        }
    }

    /**
     * 添加 RxEvent TAG 接收
     */
    fun addReceiveEventTag(rxEventTag: String) {
        RxBus.getDefault().subscribe(this, rxEventTag, object : RxBus.Callback<RxEvent>() {
            override fun onEvent(t: RxEvent?) {
                onReceiveRxEvent(t ?: return, rxEventTag)
            }
        })
    }

    /**
     * 收到事件，需要提前调用 addReceiveEventTag 添加事件
     */
    protected open fun onReceiveRxEvent(rxEvent: RxEvent, rxEventTag: String) {

    }

    /**
     * 初始化后，处于显示时，的其他非首次回调
     */
    protected open fun onResumeExceptFirst() {

    }

    override fun p2vShowSnack(msg: String?, snackBarType: Int) {

    }

    override fun p2vShowToast(msg: String?) {
        ToastUtils.showShort(msg.orEmpty())
    }

    override fun p2vShowLoading(msg: String?) {
        loading?.show(this, msg)
    }

    override fun p2vHideLoading() {
        loading?.dismiss()
    }

    override fun p2vGetStateController(): StateViewImpl {
        return stateViewImpl ?: throw NullPointerException("stateViewImpl is null !!!")
    }

    /**
     * 重试或刷新点击
     */
    override fun p2vClickStatusView(stateView: StateView, view: View) {

    }

    /**
     * 统一线程处理
     */
    protected fun <T : Any> bindTransformer(): ObservableTransformer<T, T> {
        return BluePrint.bindTransformer()
    }

    /**
     * 绑定生命周期
     */
    protected fun <T : Any> bindLifecycle(): AutoDisposeConverter<T> {
        return BluePrint.bindLifecycle(this)
    }

    /**
     * 返回键
     */
    open fun onFragmentBackPressed(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 重置，需重新加载数据
        isLazyLoaded = false
    }

    @CallSuper
    override fun onDestroy() {
        loading?.dismiss()
        loading = null

        RxBus.getDefault().unregister(this)
        super.onDestroy()
    }


}