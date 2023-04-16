@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.floater.widget.stateview.StateViewLiveData

/**
 * BaseModelActivity
 *
 * @author why
 * @since 2022/8/3
 **/
abstract class BaseViewModelActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity() {
    protected lateinit var binding: VB

    protected val viewModel: VM by createViewModel()

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

        viewModel.loadingDialogState.observe(this) {
            if (it.type == LoadingState.STATE_STARTING) {
                loadingDialog.show(this, viewModel.loadingDialogTips)
            } else {
                loadingDialog.dismiss()
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

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.onViewDestroy()
        viewModel.onDestroy()
    }
}