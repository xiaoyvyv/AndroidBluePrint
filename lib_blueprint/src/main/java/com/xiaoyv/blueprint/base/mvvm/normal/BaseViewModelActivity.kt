@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.widget.stateview.StateViewLiveData

/**
 * BaseModelActivity
 *
 * @author why
 * @since 2022/8/3
 **/
abstract class BaseViewModelActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity() {
    protected lateinit var binding: VB

    val viewModel: VM by createViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewExt()
        viewModel.onAttach(this)
        viewModel.onViewCreated()
    }

    private fun initViewExt() {
        viewModel.loadingViewState.observe(this) {
            when (it.type) {
                StateViewLiveData.StateType.STATE_LOADING -> {
                    loadingStateView.showLoadingView()
                }

                StateViewLiveData.StateType.STATE_TIPS -> {
                    loadingStateView.showTipView(it.tipMsg, it.tipImage)
                }

                StateViewLiveData.StateType.STATE_HIDE -> {
                    loadingStateView.showNormalView()
                }
            }
        }

        viewModel.loadingDialogLiveData.observe(this) {
            if (it.type == LoadingState.STATE_STARTING) {
                loadingDialog.canCancelable = viewModel.loadingDialogCancelable
                loadingDialog.showLoading(this, viewModel.loadingDialogTips)
            } else {
                loadingDialog.dismissLoading()
            }
        }
    }

    @CallSuper
    override fun createContentView(): View {
        binding = createBinding()
        return binding.root
    }

    abstract override fun initView()

    abstract override fun initData()

    fun SwipeRefreshLayout.initRefresh(autoRefreshOnLoading: () -> Boolean = { false }) {
        isEnabled = true

        viewModel.loadingViewState.observe(this@BaseViewModelActivity) {
            if (it.type != StateViewLiveData.StateType.STATE_LOADING) {
                isRefreshing = false
                isEnabled = true
            } else {
                if (autoRefreshOnLoading()) {
                    isEnabled = true
                    isRefreshing = true
                } else if (isRefreshing.not()) {
                    isEnabled = false
                }
            }
        }

        viewModel.loadingDialogLiveData.observe(this@BaseViewModelActivity) {
            if (it.type == LoadingState.STATE_ENDING) {
                isRefreshing = false
                isEnabled = true
            } else {
                if (autoRefreshOnLoading()) {
                    isEnabled = true
                    isRefreshing = true
                } else if (isRefreshing.not()) {
                    isEnabled = false
                }
            }
        }
    }

    inline fun initLoadingFinishState(crossinline onLoadingStateFinish: () -> Unit = { }) {
        viewModel.loadingViewState.observe(this@BaseViewModelActivity) {
            if (it.type != StateViewLiveData.StateType.STATE_LOADING) {
                onLoadingStateFinish()
            }
        }

        viewModel.loadingDialogLiveData.observe(this@BaseViewModelActivity) {
            if (it.type == LoadingState.STATE_ENDING) {
                onLoadingStateFinish()
            }
        }
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.onViewDestroy()
        viewModel.onDestroy()
    }
}