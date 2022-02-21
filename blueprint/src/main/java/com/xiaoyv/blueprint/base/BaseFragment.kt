@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import autodispose2.AutoDisposeConverter
import com.blankj.utilcode.util.LogUtils
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.R
import com.xiaoyv.blueprint.databinding.BpFragmentRootBinding
import com.xiaoyv.widget.dialog.loading.UiLoadingDialog
import io.reactivex.rxjava3.core.ObservableTransformer

/**
 * BaseFragment
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseFragment : Fragment(), IBaseView {
    private lateinit var rootBinding: BpFragmentRootBinding
    private lateinit var loading: UiLoadingDialog
    protected lateinit var requireActivity: FragmentActivity

    val requireStateView: StateView
        get() = vGetStateView()

    /**
     * 懒加载是否完成
     */
    var isLazyLoaded = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity = requireActivity()

        arguments?.also {
            initArgumentsData(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = BpFragmentRootBinding.inflate(layoutInflater, container, false)

        // 设置视图
        createContentView()?.also {
            rootBinding.flRoot.addView(it)
        }

        // 初始化相关回调
        initBaseView()
        initView()
        return rootBinding.root
    }

    private fun initBaseView() {
        loading = UiLoadingDialog()
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
        if (!isLazyLoaded && !isHidden) {
            LogUtils.i("${javaClass.simpleName}_LazyLoad")
            initData()
            initEvent()
            initListener()
            initFinish()
            isLazyLoaded = true
        }
    }

    override fun p2vShowToast(msg: String?) {

    }

    override fun p2vShowSnack(msg: String?, snackBarType: Int) {

    }


    override fun p2vShowLoading(msg: String?) {
        if (!isAdded || isHidden) {
            return
        }
        loading.dismiss()
        loading.showWithMsg(childFragmentManager, msg)
    }

    override fun p2vHideLoading() {
        if (!isAdded || isHidden) {
            return
        }
        loading.dismiss()
    }


    override fun p2vShowNormalView() {
        requireStateView.showContent()
    }

    override fun p2vShowEmptyView() {
        requireStateView.showEmpty()
    }

    override fun p2vShowTipView(msg: String?) {
        requireStateView.showEmpty()
    }

    override fun p2vShowLoadingView() {
        requireStateView.showLoading()
    }

    override fun p2vShowRetryView() {
        requireStateView.showRetry()
    }

    override fun p2vShowRetryView(msg: String?) {
        requireStateView.showRetry()
    }

    override fun p2vShowRetryView(msg: String?, btText: String?) {
        requireStateView.showRetry()
    }

    override fun vGetStateView() = rootBinding.csvStatus.also {
        it.emptyResource = R.layout.ui_view_state_empty
        it.loadingResource = R.layout.ui_view_state_loading
        it.retryResource = R.layout.ui_view_state_retry
        it.onRetryClickListener = object : StateView.OnRetryClickListener {
            override fun onRetryClick() {
                vRetryClick()
            }
        }

        it.updateLayoutParams<FrameLayout.LayoutParams> {
            topMargin = stateViewTopMargin()
        }
    }

    override fun vRetryClick() {
        LogUtils.i("vRetryClick")
    }

    /**
     * 返回键
     */
    fun onFragmentBackPressed(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 重置，需重新加载数据
        isLazyLoaded = false
    }

    @CallSuper
    override fun onDestroy() {
        loading.dismiss()
        super.onDestroy()
    }

    /**
     * 状态布局顶部缩进
     */
    protected open fun stateViewTopMargin(): Int = 0

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


}