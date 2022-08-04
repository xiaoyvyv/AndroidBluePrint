@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.nukc.stateview.StateView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.xiaoyv.blueprint.databinding.BpFragmentRootBinding
import com.xiaoyv.widget.dialog.UiLoadingDialog
import com.xiaoyv.widget.stateview.StateViewImpl
import com.xiaoyv.widget.utils.removeFromParent
import java.lang.ref.WeakReference

/**
 * BaseFragment
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseFragment : Fragment(), IBaseView {
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
            rootBinding = BpFragmentRootBinding.inflate(inflater, container, false)

            val fragmentContentView = createContentView(inflater, rootBinding.flRoot)
            if (fragmentContentView != null) {
                rootBinding.flRoot.addView(fragmentContentView)
            }
            rootView = rootBinding.root
        }
        rootView.removeFromParent()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化相关回调
        initBaseView()
        initView()
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
                stateView =
                    createStateView(
                        hostActivity,
                        rootBinding.content,
                        this@BaseFragment::p2vClickStatusView
                    )
                reference = WeakReference(stateView)
                return stateView
            }
        }
    }

    protected abstract fun createContentView(inflater: LayoutInflater, parent: FrameLayout): View?
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
    fun <T> addReceiveEventTag(key: String, type: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(key, type).observe(this, observer)
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
        super.onDestroy()
    }
}