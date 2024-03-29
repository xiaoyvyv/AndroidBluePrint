@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.LogUtils
import com.github.nukc.stateview.StateView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.xiaoyv.blueprint.databinding.BpFragmentRootBinding
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.dialog.UiLoadingDialog
import com.xiaoyv.widget.stateview.EmptyStateController
import com.xiaoyv.widget.stateview.IStateController

/**
 * BaseFragment
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseFragment : Fragment(), IBaseView {
    private lateinit var rootBinding: BpFragmentRootBinding
    protected lateinit var hostActivity: FragmentActivity

    /**
     * Loading 相关控制
     */
    protected lateinit var loadingDialog: UiDialog
    protected lateinit var loadingStateView: IStateController

    override val stateController: IStateController
        get() = loadingStateView

    open val nestingState: Boolean = false

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
        // 设置嵌套视图
        return if (nestingState) {
            rootBinding = BpFragmentRootBinding.inflate(inflater, container, false)
            val fragmentContentView = createContentView(inflater, rootBinding.flRoot)
            if (fragmentContentView != null) {
                rootBinding.flRoot.addView(fragmentContentView)
            }
            rootBinding.root
        } else {
            createContentView(inflater, container)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化相关回调
        initBaseView()
        initView()
    }

    private fun initBaseView() {
        // Loading
        loadingDialog = createLoadingDialog()
        loadingStateView = onCreateStateController()
    }

    protected open fun createLoadingDialog(): UiDialog = UiLoadingDialog()

    /**
     * 创建 IStateController
     */
    override fun onCreateStateController(): IStateController = EmptyStateController()

    protected abstract fun createContentView(inflater: LayoutInflater, parent: ViewGroup?): View?
    protected open fun initArgumentsData(arguments: Bundle) {}
    protected abstract fun initView()
    protected abstract fun initData()
    protected open fun initEvent() {}
    protected open fun initListener() {}
    protected open fun LifecycleOwner.initViewObserver() {}
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
            viewLifecycleOwner.initViewObserver()
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

    override fun showSnack(msg: String?, snackBarType: Int) {

    }

    override fun showToast(msg: String?) {
        showToastCompat(msg.orEmpty())
    }

    override fun showLoading(msg: String?) {
        loadingDialog.showLoading(requireActivity(), msg)
    }

    override fun hideLoading() {
        loadingDialog.dismissLoading()
    }

    /**
     * 重试或刷新点击
     */
    override fun onClickStateView(stateView: StateView, view: View) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 重置，需重新加载数据
        isLazyLoaded = false
    }

    @CallSuper
    override fun onDestroy() {
        loadingDialog.dismissLoading()
        super.onDestroy()
    }
}