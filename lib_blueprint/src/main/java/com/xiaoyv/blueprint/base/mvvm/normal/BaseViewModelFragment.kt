@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseFragment
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.widget.stateview.StateViewLiveData

/**
 * BaseViewModelFragment
 *
 * @author why
 * @since 2022/8/5
 **/
abstract class BaseViewModelFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment() {
    protected lateinit var binding: VB

    protected val viewModel: VM by createViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.onAttach(context)
    }

    @CallSuper
    override fun createContentView(inflater: LayoutInflater, parent: FrameLayout): View {
        binding = createBinding(layoutInflater, parent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewExt()
        initObserver()
        viewModel.onViewCreated()
    }

    private fun initViewExt() {
        viewModel.loadingViewState.observe(viewLifecycleOwner) {
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

        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner) {
            if (it.type == LoadingState.STATE_STARTING) {
                loadingDialog.canCancelable = viewModel.loadingDialogCancelable
                loadingDialog.show(requireActivity(), viewModel.loadingDialogTips)
            } else {
                loadingDialog.dismiss()
            }
        }
    }

    abstract override fun initView()

    abstract override fun initData()

    protected open fun initObserver() {}

    fun SwipeRefreshLayout.initRefresh() {
        viewModel.loadingViewState.observe(this@BaseViewModelFragment.viewLifecycleOwner) {
            if (it.type != StateViewLiveData.StateType.STATE_LOADING) {
                isRefreshing = false
            }
        }

        viewModel.loadingDialogLiveData.observe(this@BaseViewModelFragment.viewLifecycleOwner) {
            if (it.type == LoadingState.STATE_ENDING) {
                isRefreshing = false
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onViewDestroy()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}